package br.com.basis.jenkins.rancher.pipeline.action;

public enum StatusRancher {

    UPGRADED, ACTIVE;

    public static boolean isEqual(StatusRancher statusRancher, String status){
        return statusRancher.toString().equalsIgnoreCase(status);
    }
}
