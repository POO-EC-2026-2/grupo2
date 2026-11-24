import java.time.LocalDateTime;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;

/**
 * Consulta marcada por um paciente com um médico.
 */
@DatabaseTable(tableName = "Consulta")
public class Consulta
{
    public static final String AGENDADA = "AGENDADA";
    public static final String CANCELADA = "CANCELADA";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, index = true)
    private int medicoId;

    @DatabaseField(canBeNull = false, index = true)
    private int pacienteId;

    /** "yyyy-MM-ddTHH:mm" (ISO) - ordena corretamente como texto */
    @DatabaseField(canBeNull = false, index = true)
    private String dataHora;

    @DatabaseField(canBeNull = false)
    private int duracaoMinutos;

    @DatabaseField(canBeNull = false)
    private String status;

    /** construtor vazio exigido pelo ORMLite */
    public Consulta() { }

    public Consulta(int medicoId, int pacienteId, LocalDateTime dataHora, int duracaoMinutos) {
        this.medicoId = medicoId;
        this.pacienteId = pacienteId;
        this.dataHora = dataHora.toString();
        this.duracaoMinutos = duracaoMinutos;
        this.status = AGENDADA;
    }

    public int getId() { return id; }
    public int getMedicoId() { return medicoId; }
    public int getPacienteId() { return pacienteId; }
    public LocalDateTime getDataHora() { return LocalDateTime.parse(dataHora); }
    public LocalDateTime getFim() { return getDataHora().plusMinutes(duracaoMinutos); }
    public int getDuracaoMinutos() { return duracaoMinutos; }
    public String getStatus() { return status; }
    public boolean isAtiva() { return AGENDADA.equals(status); }
    public void cancelar() { this.status = CANCELADA; }

    @Override
    public String toString() {
        return "Consulta #" + id + " em " + dataHora.replace('T', ' ') + " [" + status + "]";
    }
}
