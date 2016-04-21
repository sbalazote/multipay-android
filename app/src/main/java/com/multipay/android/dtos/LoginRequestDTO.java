package com.multipay.android.dtos;

import java.io.Serializable;

public class LoginRequestDTO implements Serializable {

        private static final long serialVersionUID = 1L;

        private String userEmail;
        private String userPassword;
        private String mobileId;
		
        public String getUserEmail() {
			return userEmail;
		}
		
        public void setUserEmail(String userEmail) {
			this.userEmail = userEmail;
		}
		
        public String getUserPassword() {
			return userPassword;
		}
		
        public void setUserPassword(String userPassword) {
			this.userPassword = userPassword;
		}
		
        public String getMobileId() {
			return mobileId;
		}
		
        public void setMobileId(String mobileId) {
			this.mobileId = mobileId;
		}
}