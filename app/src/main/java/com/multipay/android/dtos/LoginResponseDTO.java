package com.multipay.android.dtos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginResponseDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        @SerializedName("Valid")
        private Boolean valid;
        @SerializedName("Message")
        private String message;
        @SerializedName("UserId")
        private Integer userId;
        @SerializedName("Username")
        private String userName;
        @SerializedName("UserEmail")
        private String userEmail;

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

        public String getUserEmail() {
                return userEmail;
        }

        public void setUserEmail(String userEmail) {
                this.userEmail = userEmail;
        }
}