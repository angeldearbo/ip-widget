package com.winca.jmtempcool;

public class CanData {

    static CanData data = null;
    public int Outside_T = '\uffff';

    protected CanData() {
        data = this;
    }

    public static CanData getData() {
        if (data == null) {
            data = new CanData();
        }
        return data;
    }

    public int GetOutSideT() {
        return this.Outside_T == '\uffff' ? 0 : this.Outside_T;
    }
}
