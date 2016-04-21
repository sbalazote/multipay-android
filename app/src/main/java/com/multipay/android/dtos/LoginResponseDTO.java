package com.multipay.android.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class LoginResponseDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        //@JsonProperty(value="Valid")
        @SerializedName("Valid")
        private Boolean valid;
        //@JsonProperty(value="Message")
        @SerializedName("Message")
        private String message;
        //@JsonProperty(value="UserId")
        @SerializedName("UserId")
        private Integer userId;
        //@JsonProperty(value="Username")
        @SerializedName("Username")
        private String userName;

        public Boolean getValid() {
                return this.valid;
        }

        public void setValid(Boolean valid) {
                this.valid = valid;
        }

        public String getMessage() {
                return this.message;
        }

        public void setMessage(String message) {
                this.message = message;
        }

        public Integer getUserId() {
                return this.userId;
        }

        public void setUserId(Integer userId) {
                this.userId = userId;
        }

        public String getUserName() {
                return this.userName;
        }

        public void setUserName(String userName) {
                this.userName = userName;
        }
}