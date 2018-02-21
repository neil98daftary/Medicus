package com.nmd.medicus;

/**
 * Created by neil on 21/2/18.
 */

public class ListModel {
    private  String CompanyName="";
    private  String Image="";
    private  String Url="";

    /*********** Set Methods ******************/

    public void setCompanyName(String CompanyName)
    {
        this.CompanyName = CompanyName;
    }

    public void setImage(String Image)
    {
        this.Image = "ic_launcher_round";
    }

    public void setUrl(String Url)
    {
        this.Url = Url;
    }

    /*********** Get Methods ****************/

    public String getCompanyName()
    {
        return this.CompanyName;
    }

    public String getImage()
    {
        return this.Image;
    }

    public String getUrl()
    {
        return this.Url;
    }
}
