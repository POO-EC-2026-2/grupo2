import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Regras de negócio do agendamento (camada "Model" do MVC).
 *
 * Os Controllers do JavaFX só chamam os métodos daqui:
 *  - tela do médico:   adicionarDisponibilidade / listarDisponibilidades / removerDisponibilidade
 *  - tela do paciente: listarHorariosDisponiveis -> agendar
 *  - ambos:            consultasDoPaciente / consultasDoMedico / cancelar
 */
public class AgendamentoService
{
    private final DisponibilidadeRepository disponibilidades;
    private final ConsultaRepository consultas;

    public AgendamentoService(Database database) {
        this.disponibilidades = new DisponibilidadeRepository(database);
        this.consultas = new ConsultaRepository(database);
    }

    // ------------------------------------------------------------------ médico

    /** O médico cadastra um bloco semanal de atendimento. */
    public DisponibilidadeMedico adicionarDisponibilidade(int medicoId, DayOfWeek dia,
            LocalTime inicio, LocalTime fim, int duracaoMinutos,
            LocalDate validoDe, LocalDate validoAte) {
        for (DisponibilidadeMedico d : disponibilidades.loadByMedico(medicoId)) {
            if (d.getDiaSemana() == dia && sobrepoeHorario(d, inicio, fim) && sobrepoePeriodo(d, validoDe, validoAte))
                throw new IllegalArgumentException("Já existe disponibilidade nesse horário: " + d);
        }
        return disponibilidades.create(new DisponibilidadeMedico(
                medicoId, dia, inicio, fim, duracaoMinutos, validoDe, validoAte));
    }

    public List<DisponibilidadeMedico> listarDisponibilidades(int medicoId) {
        return disponibilidades.loadByMedico(medicoId);
    }

    public void removerDisponibilidade(DisponibilidadeMedico d) {
        disponibilidades.delete(d);
    }

    // ---------------------------------------------------------------- paciente

    /**
     * Horários livres do médico entre as datas (inclusivas), agrupados por dia.
     * Já exclui horários ocupados e horários que já passaram.
     */
    public Map<LocalDate, List<LocalTime>> listarHorariosDisponiveis(int medicoId, LocalDate de, LocalDate ate) {
        Map<LocalDate, List<LocalTime>> resultado = new TreeMap<LocalDate, List<LocalTime>>();
        List<DisponibilidadeMedico> agenda = disponibilidades.loadByMedico(medicoId);
        List<Consulta> ocupadas = consultas.loadAtivasDoMedico(medicoId, de, ate);
        LocalDateTime agora = LocalDateTime.now();

        for (LocalDate dia = de; !dia.isAfter(ate); dia = dia.plusDays(1)) {
            List<LocalTime> livres = new ArrayList<LocalTime>();
            for (DisponibilidadeMedico d : agenda) {
                if (!d.valeNaData(dia)) continue;
                LocalTime hora = d.getHoraInicio();
                // enquanto a consulta inteira couber dentro do bloco
                while (!hora.plusMinutes(d.getDuracaoMinutos()).isAfter(d.getHoraFim())
                        && hora.isBefore(d.getHoraFim())) {
                    LocalDateTime inicio = dia.atTime(hora);
                    LocalDateTime fim = inicio.plusMinutes(d.getDuracaoMinutos());
                    if (inicio.isAfter(agora) && !conflita(ocupadas, inicio, fim))
                        livres.add(hora);
                    hora = hora.plusMinutes(d.getDuracaoMinutos());
                }
            }
            if (!livres.isEmpty()) {
                livres.sort(null);
                resultado.put(dia, livres);
            }
        }
        return resultado;
    }

    /**
     * Agenda a consulta. Valida que o horário é um slot real da agenda do médico
     * e que ainda está livre (proteção contra dois pacientes no mesmo horário).
     */
    public synchronized Consulta agendar(int medicoId, int pacienteId, LocalDateTime dataHora) {
        LocalDate dia = dataHora.toLocalDate();
        DisponibilidadeMedico bloco = encontrarBloco(medicoId, dataHora);
        if (bloco == null)
            throw new IllegalArgumentException("Horário fora da agenda do médico");
        if (!dataHora.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("Não é possível agendar no passado");

        LocalDateTime fim = dataHora.plusMinutes(bloco.getDuracaoMinutos());
        if (conflita(consultas.loadAtivasDoMedico(medicoId, dia, dia), dataHora, fim))
            throw new IllegalStateException("Horário já está ocupado");

        try {
            return consultas.create(new Consulta(medicoId, pacienteId, dataHora, bloco.getDuracaoMinutos()));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar consulta", e);
        }
    }

    public void cancelar(Consulta consulta) {
        consulta.cancelar();
        try {
            consultas.update(consulta);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cancelar consulta", e);
        }
    }

    public List<Consulta> consultasDoPaciente(int pacienteId) {
        return consultas.loadDoPaciente(pacienteId);
    }

    public List<Consulta> consultasDoMedico(int medicoId) {
        return consultas.loadDoMedico(medicoId);
    }

    // ---------------------------------------------------------------- auxiliares

    /** Bloco da agenda em que dataHora é exatamente o início de um slot. */
    private DisponibilidadeMedico encontrarBloco(int medicoId, LocalDateTime dataHora) {
        LocalTime hora = dataHora.toLocalTime();
        for (DisponibilidadeMedico d : disponibilidades.loadByMedico(medicoId)) {
            if (!d.valeNaData(dataHora.toLocalDate())) continue;
            if (hora.isBefore(d.getHoraInicio())) continue;
            if (hora.plusMinutes(d.getDuracaoMinutos()).isAfter(d.getHoraFim())) continue;
            long minutosDesdeInicio = java.time.Duration.between(d.getHoraInicio(), hora).toMinutes();
            if (minutosDesdeInicio % d.getDuracaoMinutos() == 0) return d;
        }
        return null;
    }

    private static boolean conflita(List<Consulta> ocupadas, LocalDateTime inicio, LocalDateTime fim) {
        for (Consulta c : ocupadas) {
            if (c.isAtiva() && inicio.isBefore(c.getFim()) && c.getDataHora().isBefore(fim))
                return true;
        }
        return false;
    }

    private static boolean sobrepoeHorario(DisponibilidadeMedico d, LocalTime inicio, LocalTime fim) {
        return inicio.isBefore(d.getHoraFim()) && d.getHoraInicio().isBefore(fim);
    }

    private static boolean sobrepoePeriodo(DisponibilidadeMedico d, LocalDate de, LocalDate ate) {
        LocalDate dDe = d.getValidoDe(), dAte = d.getValidoAte();
        boolean antes = ate != null && dDe != null && ate.isBefore(dDe);
        boolean depois = de != null && dAte != null && de.isAfter(dAte);
        return !(antes || depois);
    }
}
