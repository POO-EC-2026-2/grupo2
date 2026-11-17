import java.util.Date;
import java.text.SimpleDateFormat;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.DataType;

public abstract class Usuario
{
    @DatabaseField(generatedId = true)
    protected int idade;
    
    @DatabaseField
    protected String nome;
    
    @DatabaseField
    protected String CPF;
    
    @DatabaseField(dataType=DataType.DATE)
    protected Date birthday;  
    
    @DatabaseField
    protected String endereco;
    
    @DatabaseField
    protected String sexo;
    
    @DatabaseField
    protected String senha;

}