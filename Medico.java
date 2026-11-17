import java.text.SimpleDateFormat;
import java.util.Date;

public class Medico extends Usuario
{
    private String crm;
    private String ufcrm;
    
    
      public String printBirthday() {
        SimpleDateFormat dateFor = new SimpleDateFormat("dd/MM/yyyy");
        return dateFor.format(birthday);
    }

//Start GetterSetterExtension Source Code

    /**GET Method Propertie id*/
    public int getidade(){
        return this.idade;
    }//end method getId

    /**SET Method Propertie id*/
    public void setId(int id){
        this.idade = id;
    }//end method setId

    /**GET Method Propertie fullName*/
    public String getnome(){
        return this.nome;
    }//end method getFullName

    /**SET Method Propertie fullName*/
    public void setnome(String nome){
        this.nome = nome;
    }//end method setFullName

    /**GET Method Propertie registration*/
    public String getCPF(){
        return this.CPF;
    }//end method getRegistration

    /**SET Method Propertie registration*/
    public void setCPF(String CPF){
        this.CPF = CPF;
    }//end method setRegistration

    /**GET Method Propertie birthday*/
    public Date getBirthday(){
        return this.birthday;
    }//end method getBirthday

    /**SET Method Propertie birthday*/
    public void setBirthday(Date birthday){
        this.birthday = birthday;
    }//end method setBirthday
    
    
    public void setendereco(String endereco){
        this.endereco=endereco;
    }
    
    public String getendereco(){
        return this.endereco;
    }
    
    public void setsexo(String sexo){
        this.sexo=sexo;
        
    }
    public String getsexo(){
        return this.sexo;
    }
    
    public String getCRM(){
        return this.crm;
    }
    public void setCRM(String crm){
        this.crm=crm;
    }
    public String getufcrm(){
        return this.ufcrm;
    }
    public void setufcrm(String ufcrm){
        this.ufcrm=ufcrm;
    }
}