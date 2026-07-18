package it.univaq.soccorsoweb.data.model.impl;

import it.univaq.framework.data.DataItemImpl;
import it.univaq.soccorsoweb.data.model.RichiestaSoccorso;

import java.time.LocalDateTime;

public class RichiestaSoccorsoImpl extends DataItemImpl<Integer> implements RichiestaSoccorso {

    private String descrizione;
    private String coordinate;
    private LocalDateTime dataOraInvio;
    private String stato;
    private String tokenConvalida;
    private String ip;
    private byte[] foto;
    private String segnalante;
    private String email_segnalante;

    public RichiestaSoccorsoImpl() {
        this.descrizione = "";
        this.coordinate = "";
        this.dataOraInvio = null;
        this.stato = "";
        this.tokenConvalida = "";
        this.ip = "";
        this.foto = null;
        this.segnalante = "";
        this.email_segnalante = "";
    }

    @Override
    public String getDescrizione() {
        return descrizione;
    }

    @Override
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    @Override
    public String getCoordinate() {
        return coordinate;
    }

    @Override
    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public LocalDateTime getDataOraInvio() {
        return dataOraInvio;
    }

    @Override
    public void setDataOraInvio(LocalDateTime dataOraInvio) {
        this.dataOraInvio = dataOraInvio;
    }

    @Override
    public String getStato() {
        return stato;
    }

    @Override
    public void setStato(String stato) {
        this.stato = stato;
    }

    @Override
    public String getTokenConvalida() {
        return tokenConvalida;
    }

    @Override
    public void setTokenConvalida(String tokenConvalida) {
        this.tokenConvalida = tokenConvalida;
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public byte[] getFoto() {
        return foto;
    }

    @Override
    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    @Override
    public String getSegnalante() {
        return segnalante;
    }

    @Override
    public void setSegnalante(String segnalante) {
        this.segnalante = segnalante;
    }

    @Override
    public String getEmail_segnalante() {
        return email_segnalante;
    }

    @Override
    public void setEmail_segnalante(String email_segnalante) {
        this.email_segnalante = email_segnalante;
    }

}
