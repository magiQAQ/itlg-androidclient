package com.itlg.client.bean;

public class ProductInfoModel {
    private ProductInfo productInfo;     //产品信息
    private int typeOne;                 //产品一级分类id
    private String typeOneName;             //产品一级分类名
    private int typeTwo;                 //产品二级分类id
    private String typeTwoName;             //产品二级分类名

    public ProductInfo getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(ProductInfo productInfo) {
        this.productInfo = productInfo;
    }

    public int getTypeOne() {
        return typeOne;
    }

    public void setTypeOne(int typeOne) {
        this.typeOne = typeOne;
    }

    public String getTypeOneName() {
        return typeOneName;
    }

    public void setTypeOneName(String typeOneName) {
        this.typeOneName = typeOneName;
    }

    public int getTypeTwo() {
        return typeTwo;
    }

    public void setTypeTwo(int typeTwo) {
        this.typeTwo = typeTwo;
    }

    public String getTypeTwoName() {
        return typeTwoName;
    }

    public void setTypeTwoName(String typeTwoName) {
        this.typeTwoName = typeTwoName;
    }
}
