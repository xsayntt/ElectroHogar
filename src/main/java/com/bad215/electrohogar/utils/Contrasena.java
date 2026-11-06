/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bad215.electrohogar.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Carlos
 */
public class Contrasena {
        public static String hashear(String contraseniaPlana) {
        return BCrypt.hashpw(contraseniaPlana, BCrypt.gensalt(12));
    }

    public static boolean verificarContrasenia(String contraseniaPlana, String hash){
        return BCrypt.checkpw(contraseniaPlana, hash);
    }
    
}
