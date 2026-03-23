package com.ship.weeklycommits.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "org_settings")
public class OrgSettings {

    @Id
    @Column(name = "org_id")
    private String orgId;

    @Column(nullable = false)
    private String timezone = "America/New_York";

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
