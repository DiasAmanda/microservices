package br.com.amanda.matera.edge.filter;

import javax.ws.rs.QueryParam;

public class MovieFilter {

    @QueryParam("title")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
