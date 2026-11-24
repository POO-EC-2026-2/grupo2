import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DisponibilidadeRepository
{
    private Dao<DisponibilidadeMedico, Integer> dao;

    public DisponibilidadeRepository(Database database) {
        try {
            dao = DaoManager.createDao(database.getConnection(), DisponibilidadeMedico.class);
            TableUtils.createTableIfNotExists(database.getConnection(), DisponibilidadeMedico.class);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public DisponibilidadeMedico create(DisponibilidadeMedico d) {
        try {
            dao.create(d);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return d;
    }

    public void delete(DisponibilidadeMedico d) {
        try {
            dao.delete(d);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public List<DisponibilidadeMedico> loadByMedico(int medicoId) {
        try {
            return dao.queryForEq("medicoId", medicoId);
        } catch (SQLException e) {
            System.out.println(e);
            return new ArrayList<DisponibilidadeMedico>();
        }
    }
}
