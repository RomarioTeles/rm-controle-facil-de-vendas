package br.com.rm.cfv.bottomsheets;

public interface IBottomSheetOptions {

    default void buttonSheetLista(Object item){

    }

    default void buttonSheetAdiciona(Object item){

    }

    default void buttonSheetRemove(Object item, int position){

    }

    default void buttonSheetEdita(Object item){
    }

    default void buttonSheetSeleciona(Object item){
    }

}
