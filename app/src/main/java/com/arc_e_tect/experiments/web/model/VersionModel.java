package com.arc_e_tect.experiments.web.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
public class VersionModel extends RepresentationModel<VersionModel> {
    private String versionName;
    private double versionCode;
}