import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;

/**
 * Bloco de horário em que o médico atende, definido pelo próprio médico.
 *
 * Exemplo: "toda segunda-feira, das 08:00 às 12:00, consultas de 30 min,
 * válido de 01/12/2026 até 28/02/2027".
 *
 * O sistema quebra esse bloco em horários (slots) que o paciente pode escolher.
 */
@DatabaseTable(tableName = "DisponibilidadeMedico")
public class DisponibilidadeMedico
{
    @DatabaseField(generatedId = true)
    private int id;

    /** id do médico dono desta agenda */
    @DatabaseField(canBeNull = false, index = true)
    private int medicoId;

    /** 1 = segunda ... 7 = domingo (padrão java.time.DayOfWeek) */
    @DatabaseField(canBeNull = false)
    private int diaSemana;

    /** "HH:mm" */
    @DatabaseField(canBeNull = false)
    private String horaInicio;

    /** "HH:mm" */
    @DatabaseField(canBeNull = false)
    private String horaFim;

    /** duração de cada consulta em minutos */
    @DatabaseField(canBeNull = false)
    private int duracaoMinutos;

    /** "yyyy-MM-dd" - a partir de quando vale (null = sem limite) */
    @DatabaseField
    private String validoDe;

    /** "yyyy-MM-dd" - até quando vale (null = sem limite) */
    @DatabaseField
    private String validoAte;

    /** construtor vazio exigido pelo ORMLite */
    public DisponibilidadeMedico() { }

    public DisponibilidadeMedico(int medicoId, DayOfWeek dia, LocalTime inicio, LocalTime fim,
                                 int duracaoMinutos, LocalDate validoDe, LocalDate validoAte) {
        if (!fim.isAfter(inicio))
            throw new IllegalArgumentException("Hora final deve ser depois da hora inicial");
        if (duracaoMinutos <= 0)
            throw new IllegalArgumentException("Duração da consulta deve ser positiva");
        if (validoDe != null && validoAte != null && validoAte.isBefore(validoDe))
            throw new IllegalArgumentException("Período de validade inválido");
        this.medicoId = medicoId;
        this.diaSemana = dia.getValue();
        this.horaInicio = inicio.toString();
        this.horaFim = fim.toString();
        this.duracaoMinutos = duracaoMinutos;
        this.validoDe = validoDe == null ? null : validoDe.toString();
        this.validoAte = validoAte == null ? null : validoAte.toString();
    }

    /** Esta disponibilidade vale para a data informada? */
    public boolean valeNaData(LocalDate data) {
        if (data.getDayOfWeek().getValue() != diaSemana) return false;
        if (validoDe != null && data.isBefore(LocalDate.parse(validoDe))) return false;
        if (validoAte != null && data.isAfter(LocalDate.parse(validoAte))) return false;
        return true;
    }

    public int getId() { return id; }
    public int getMedicoId() { return medicoId; }
    public DayOfWeek getDiaSemana() { return DayOfWeek.of(diaSemana); }
    public LocalTime getHoraInicio() { return LocalTime.parse(horaInicio); }
    public LocalTime getHoraFim() { return LocalTime.parse(horaFim); }
    public int getDuracaoMinutos() { return duracaoMinutos; }
    public LocalDate getValidoDe() { return validoDe == null ? null : LocalDate.parse(validoDe); }
    public LocalDate getValidoAte() { return validoAte == null ? null : LocalDate.parse(validoAte); }

    @Override
    public String toString() {
        return getDiaSemana() + " " + horaInicio + "-" + horaFim + " (" + duracaoMinutos + " min)";
    }
}
