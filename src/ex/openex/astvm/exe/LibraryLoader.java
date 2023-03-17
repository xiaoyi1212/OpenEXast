package ex.openex.astvm.exe;

import ex.openex.astvm.lib.*;

import java.util.ArrayList;

public class LibraryLoader {
    public static ArrayList<RuntimeLibrary> getLibs(){
        ArrayList<RuntimeLibrary> rls = new ArrayList<>();
        rls.add(new Sys());
        rls.add(new Array());
        rls.add(new Type());
        rls.add(new File());
        rls.add(new StringLib());
        return rls;
    }
}
