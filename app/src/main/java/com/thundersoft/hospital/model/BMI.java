package com.thundersoft.hospital.model;

public class BMI {

    /**
     * status : 0
     * msg : ok
     * result : {"bmi":"21.6","normbmi":"18.5～23.9","idealweight":"68","level":"正常范围","danger":"平均水平","status":"1"}
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
        /**
         * bmi : 21.6
         * normbmi : 18.5～23.9
         * idealweight : 68
         * level : 正常范围
         * danger : 平均水平
         * status : 1
         */

        private String bmi;
        private String normbmi;
        private String idealweight;
        private String level;
        private String danger;
        private String status;

        public String getBmi() {
            return bmi;
        }

        public void setBmi(String bmi) {
            this.bmi = bmi;
        }

        public String getNormbmi() {
            return normbmi;
        }

        public void setNormbmi(String normbmi) {
            this.normbmi = normbmi;
        }

        public String getIdealweight() {
            return idealweight;
        }

        public void setIdealweight(String idealweight) {
            this.idealweight = idealweight;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getDanger() {
            return danger;
        }

        public void setDanger(String danger) {
            this.danger = danger;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
