package com.mpproject.delivery_together;
//아이템에 들어갈 데이터를 저장하는 클래스
public class RecyclerItem{
    private String titleStr;
    private String contStr;

    public void setTitle(String title)
    {
        titleStr = title;
    }
    public void setCont(String content)
    {
        contStr = content;
    }
    public String getTitle()
    {
        return titleStr;
    }
    public String getCont()
    {
        return contStr;
    }
}
