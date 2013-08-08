package com.ning.http.client.providers.apache;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 8/7/13
 * Time: 11:32 PM
 */

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChecksumClassFile{

    public <T> String sha(Class<T> clazz) throws NoSuchAlgorithmException, NotFoundException, IOException, CannotCompileException {

        MessageDigest digester = MessageDigest.getInstance("SHA");
        digester.update(getBytes(clazz));
        return new sun.misc.BASE64Encoder().encode(digester.digest());

    }

    public <T> String md5(Class<T> clazz) throws NoSuchAlgorithmException, NotFoundException, CannotCompileException, IOException {
        MessageDigest digester = MessageDigest.getInstance("MD5");
        digester.update(getBytes(clazz));
        return new BASE64Encoder().encode(digester.digest());
    }

    private <T> byte[] getBytes(Class<T> clazz) throws IOException, CannotCompileException, NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClazz = pool.get(clazz.getName());
        return ctClazz.toBytecode();
    }
}