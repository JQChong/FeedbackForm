package com.example.jingquan.survey;

public class Question {

    public enum QUESTION_TYPE {
        LSQ(1),
        FRQ(2);

        private int type;

        QUESTION_TYPE(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    private int qNumber;
    private QUESTION_TYPE qType;
    private String statement;
    private String response;

    public Question() {
    }

    public Question(int qNumber, QUESTION_TYPE qType, String str) {
        this.qNumber = qNumber;
        this.qType = qType;
        this.statement = str;
    }

    public Question(int qNumber, QUESTION_TYPE qType, String statement, String response) {
        this.qNumber = qNumber;
        this.qType = qType;
        this.statement = statement;
        this.response = response;
    }

    public int getqNumber() {
        return qNumber;
    }

    public void setqNumber(int qNumber) {
        this.qNumber = qNumber;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public QUESTION_TYPE getqType() {
        return qType;
    }

    public void setqType(QUESTION_TYPE qType) {
        this.qType = qType;
    }
}
