/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gestion.common;


import io.swagger.annotations.ApiModelProperty;

public class StandarizedApiExceptionResponse{
   @ApiModelProperty(notes = "The unique uri identifier that categorizes the error", name = "type",
           required = true, example = "/errors/authentication/not-authorized")
    private String type ="/errors/uncategorized";
    @ApiModelProperty(notes = "A brief, human-readable message about the error", name = "title", 
           required = true, example = "The user does not have autorization")
    private String title;
     @ApiModelProperty(notes = "The unique error code", name = "code", 
           required = false, example = "192")
    private String code;
      @ApiModelProperty(notes = "A human-readable explanation of the error", name = "detail", 
           required = true, example = "The user does not have the propertly persmissions to acces the "
                   + "resource, please contact with ass https://digitalthinking.biz/es/ada-enterprise-core#contactus")
    private String detail;
       @ApiModelProperty(notes = "A URI that identifies the specific occurrence of the error", name = "detail", 
           required = true, example = "/errors/authentication/not-authorized/01")
    private String instance ="/errors/uncategorized/bank";

    public StandarizedApiExceptionResponse(String title, String code, String detail) {
        super();
        this.title = title;
        this.code = code;
        this.detail = detail;
    }       
       
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

   

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return code;
    }

    public void setStatus(String status) {
        this.code = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }
}
