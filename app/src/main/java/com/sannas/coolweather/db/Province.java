package com.sannas.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/1/16.
 */
public class Province extends DataSupport{
    private int id;
    private String provinceName;    //记录省的名字
    private int provinceCode;      //记录省的代号

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }


}
