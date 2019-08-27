package com.thundersoft.hospital.model;

import java.util.List;

public class GenderMonth {

    /**
     * status : 0
     * msg : ok
     * result : {"thisyear":["2","4","5","6","7","8","9","10","11","12"],"nextyear":["1","3","6","7","8","9","10"]}
     */

    private int status;
    private String msg;
    private ResultBean result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        private List<String> thisyear;
        private List<String> nextyear;

        public List<String> getThisyear() {
            return thisyear;
        }

        public void setThisyear(List<String> thisyear) {
            this.thisyear = thisyear;
        }

        public List<String> getNextyear() {
            return nextyear;
        }

        public void setNextyear(List<String> nextyear) {
            this.nextyear = nextyear;
        }
    }
}
