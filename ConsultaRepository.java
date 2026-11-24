import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ConsultaRepository
{
    private Dao<Consulta, Integer> dao;

    public ConsultaRepository(Database database) {
        try {
            dao = DaoManager.createDao(database.getConnection(), Consulta.class);
            TableUtils.createTableIfNotExists(database.getConnection(), Consulta.class);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public Consulta create(Consulta c) throws SQLException {
        dao.create(c);
        return c;
    }

    public void update(Consulta c) throws SQLException {
        dao.update(c);
    }

    public Consulta loadFromId(int id) {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /** Consultas ativas do médico com início entre [de, ate] (datas inclusivas). */
    public List<Consulta> loadAtivasDoMedico(int medicoId, LocalDate de, LocalDate ate) {
        try {
            return dao.queryBuilder().orderBy("dataHora", true).where()
                      .eq("medicoId", medicoId).and()
                      .eq("status", Consulta.AGENDADA).and()
                      .ge("dataHora", de.atStartOfDay().toString()).and()
                      .lt("dataHora", ate.plusDays(1).atStartOfDay().toString())
                      .query();
        } catch (SQLException e) {
            System.out.println(e);
            return new ArrayList<Consulta>();
        }
    }

    public List<Consulta> loadDoPaciente(int pacienteId) {
        try {
            return dao.queryBuilder().orderBy("dataHora", true).where()
                      .eq("pacienteId", pacienteId).query();
        } catch (SQLException e) {
            System.out.println(e);
            return new ArrayList<Consulta>();
        }
    }

    public List<Consulta> loadDoMedico(int medicoId) {
        try {
            return dao.queryBuilder().orderBy("dataHora", true).where()
                      .eq("medicoId", medicoId).query();
        } catch (SQLException e) {
            System.out.println(e);
            return new ArrayList<Consulta>();
        }
    }
}
