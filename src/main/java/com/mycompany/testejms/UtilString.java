/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.testejms;

/**
 *
 * @author mpcsj
 */
public class UtilString {

    public static String obtemAtributo(String dadosJSON, String atributo) {
        int idx = dadosJSON.indexOf(atributo);
        String res = dadosJSON.substring(idx);
        res = res.split(":")[1];
        idx = res.indexOf("\"");
        res = res.substring(idx + 1);
        idx = res.indexOf("\"");
        res = res.substring(0, idx);
        return res;
    }

}
