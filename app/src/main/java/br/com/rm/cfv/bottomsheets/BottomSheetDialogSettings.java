package br.com.rm.cfv.bottomsheets;

public class BottomSheetDialogSettings {
    private String titulo = "Ações";
    private String textoListar = "Listar";
    private String textoAdicionar = "Adicionar";
    private String textoEditar = "Editar";
    private String textoRemover = "Remover";
    private String textoSelecionar = "Selecionar";

    private boolean showListar = true;
    private boolean showEditar = true;
    private boolean showAdicionar = true;
    private boolean showRemover = true;
    private boolean showSelecionar = false;
    private boolean showRemoveDialog = true;

    public BottomSheetDialogSettings() {
        super();
    }

    public BottomSheetDialogSettings(String titulo, boolean showListar, boolean showEditar, boolean showAdicionar, boolean showRemover) {
        this.showListar = showListar;
        this.showEditar = showEditar;
        this.showAdicionar = showAdicionar;
        this.showRemover = showRemover;
        this.titulo = titulo;
    }

    public void setTextoDosBotoes(String textoListar,
                                  String textoAdicionar,
                                  String textoEditar,
                                  String textoRemover) {
        this.textoAdicionar = textoAdicionar != null ? textoAdicionar : this.textoAdicionar;
        this.textoEditar = textoEditar != null ? textoEditar : this.textoEditar;
        this.textoListar = textoListar != null ? textoListar : this.textoListar;
        this.textoRemover = textoRemover != null ? textoRemover : this.textoRemover;
    }

    public boolean isShowListar() {
        return showListar;
    }

    public void setShowListar(boolean showListar) {
        this.showListar = showListar;
    }

    public boolean isShowEditar() {
        return showEditar;
    }

    public void setShowEditar(boolean showEditar) {
        this.showEditar = showEditar;
    }

    public boolean isShowAdicionar() {
        return showAdicionar;
    }

    public void setShowAdicionar(boolean showAdicionar) {
        this.showAdicionar = showAdicionar;
    }

    public boolean isShowRemover() {
        return showRemover;
    }

    public void setShowRemover(boolean showRemover) {
        this.showRemover = showRemover;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTextoListar() {
        return textoListar;
    }

    public void setTextoListar(String textoListar) {
        this.textoListar = textoListar;
    }

    public String getTextoAdicionar() {
        return textoAdicionar;
    }

    public void setTextoAdicionar(String textoAdicionar) {
        this.textoAdicionar = textoAdicionar;
    }

    public String getTextoEditar() {
        return textoEditar;
    }

    public void setTextoEditar(String textoEditar) {
        this.textoEditar = textoEditar;
    }

    public String getTextoRemover() {
        return textoRemover;
    }

    public void setTextoRemover(String textoRemover) {
        this.textoRemover = textoRemover;
    }

    public boolean isShowRemoveDialog() {
        return showRemoveDialog;
    }

    public void setShowRemoveDialog(boolean showRemoveDialog) {
        this.showRemoveDialog = showRemoveDialog;
    }

    public String getTextoSelecionar() {
        return textoSelecionar;
    }

    public void setTextoSelecionar(String textoSelecionar) {
        this.textoSelecionar = textoSelecionar;
    }

    public boolean isShowSelecionar() {
        return showSelecionar;
    }

    public void setShowSelecionar(boolean showSelecionar) {
        this.showSelecionar = showSelecionar;
    }
}
