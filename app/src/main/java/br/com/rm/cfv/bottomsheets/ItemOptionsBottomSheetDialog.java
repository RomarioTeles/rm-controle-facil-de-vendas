package br.com.rm.cfv.bottomsheets;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import br.com.rm.cfv.R;
import br.com.rm.cfv.utils.DialogConfig;
import br.com.rm.cfv.utils.DialogUtils;

public class ItemOptionsBottomSheetDialog {

    private BottomSheetDialog dialog;

    public void openDialog(Activity context, Object item, IBottomSheetOptions options) {
        openDialog(context, item, null, new BottomSheetDialogSettings(), options);
    }

    public void openDialog(final Activity context, final Object item, final Integer position, final BottomSheetDialogSettings settings, final IBottomSheetOptions options) {

        BottomSheetDialogSettings buttonSettings = settings == null ? new BottomSheetDialogSettings() : settings;

        View view = context.getLayoutInflater().inflate(R.layout.item_option_sheet_main, null);
        dialog = new BottomSheetDialog(context);
        dialog.setContentView(view);

        TextView titulo = view.findViewById(R.id.title);
        titulo.setText(buttonSettings.getTitulo());

        TextView buttonListar = view.findViewById(R.id.listar);
        hideOption(view.findViewById(R.id.listar_parent), buttonSettings.isShowListar());
        buttonListar.setText(settings.getTextoListar());

        TextView buttonAdicionar = view.findViewById(R.id.adicionar);
        hideOption(view.findViewById(R.id.adicionar_parent), buttonSettings.isShowAdicionar());
        buttonAdicionar.setText(buttonSettings.getTextoAdicionar());

        TextView buttonEditar = view.findViewById(R.id.editar);
        hideOption(view.findViewById(R.id.editar_parent), buttonSettings.isShowEditar());
        buttonEditar.setText(buttonSettings.getTextoEditar());

        TextView buttonDeletar = view.findViewById(R.id.remover);
        hideOption(view.findViewById(R.id.remover_parent), buttonSettings.isShowRemover());
        buttonDeletar.setText(buttonSettings.getTextoRemover());

        TextView buttonCancelar = view.findViewById(R.id.cancelar);

        buttonListar.setOnClickListener(v -> {
            options.buttonSheetLista(item);
            dialog.dismiss();
        });
        buttonAdicionar.setOnClickListener(v -> {
            options.buttonSheetAdiciona(item);
            dialog.dismiss();
        });
        buttonEditar.setOnClickListener(v -> {
            options.buttonSheetEdita(item);
            dialog.dismiss();
        });
        buttonDeletar.setOnClickListener(v -> {
            dialog.dismiss();
            if(settings.isShowRemoveDialog()) {
                DialogConfig configs = new DialogConfig();
                configs.setNegativeButtonListener(() -> dialog.dismiss());
                configs.setPositiveButtonListener(() -> options.buttonSheetRemove(item, position));
                configs.setShowNegativeButton(true);
                configs.setShowSubtitle(false);
                DialogUtils.Companion.showDialogConfirm(context, R.string.mensagem_confirmacao, 0, configs);
            }else{
                options.buttonSheetRemove(item, position);
            }
        });
        buttonCancelar.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void hideOption(View view, boolean isShow){
        view.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public BottomSheetDialog getDialog() {
        return dialog;
    }
}
