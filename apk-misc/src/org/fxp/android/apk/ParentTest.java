package org.fxp.android.apk;

public class ParentTest {

    public ParentTest(){
        System.out.println(this.getClass().getName());
    }
    
    
    public static void main(String[] args){
        Child c1 = new Child();
    }


}


