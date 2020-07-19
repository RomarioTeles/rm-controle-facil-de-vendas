package br.com.rm.cfv.activities.contaPagarReceber;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

import br.com.rm.cfv.R;
import br.com.rm.cfv.activities.BaseActivity;
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate;
import br.com.rm.cfv.asyncTasks.contaPagarReceber.UpdatePagamentoDebitoAsyncTask;
import br.com.rm.cfv.database.entities.PagamentoDebito;
import br.com.rm.cfv.utils.ToastUtils;
import br.com.rm.dateutils.DateFormatUtils;
import br.com.rm.numberUtils.DecimalFormatUtils;

public class DetalheParcelaDebitoActivity extends BaseActivity implements IPostExecuteInsertAndUpdate {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_parcela_debito);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            final PagamentoDebito pagamentoDebito = bundle.getParcelable("PagamentoDebito");
            int parcela = bundle.getInt("parcela");
            TextView textViewParcela = findViewById(R.id.textViewObservacao);
            textViewParcela.setText(String.valueOf(parcela));
            TextView textViewVencimento = findViewById(R.id.textViewVencimento);
            textViewVencimento.setText( DateFormatUtils.format(new Date(pagamentoDebito.getDataVencimento()), "dd/MM/yyyy"));
            TextView textViewValor = findViewById(R.id.textViewValor);
            textViewValor.setText("R$ "+ DecimalFormatUtils.decimalFormatPtBR(pagamentoDebito.getValor()));
            SwitchCompat switchCompat = findViewById(R.id.switchPagarTotal);
            final TextInputEditText textInputEditValorPagar = findViewById(R.id.textInputEditValorPagar);
            textInputEditValorPagar.setText(String.valueOf(pagamentoDebito.getValorPago()));
            if(pagamentoDebito.getValor() <= pagamentoDebito.getValorPago()){
                switchCompat.setEnabled(false);
                switchCompat.setChecked(true);
            }else {
                switchCompat.setChecked(false);
                switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        textInputEditValorPagar.setText(String.valueOf(pagamentoDebito.getValor()));
                    }
                });
            }
            fab().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!textInputEditValorPagar.getText().toString().isEmpty()) {
                        pagamentoDebito.setValorPago(Double.valueOf(textInputEditValorPagar.getText().toString()));
                        new UpdatePagamentoDebitoAsyncTask(
                                getCfvApplication().getDataBase(),
                                DetalheParcelaDebitoActivity.this).execute(pagamentoDebito);
                    }
                }
            });
        }else{
            hideFab();
        }
    }

    @NotNull
    @Override
    public String getToobarTitle() {
        return "";
    }

    @Override
    public void afterInsert(@Nullable Object result) {

    }

    @Override
    public void afterUpdate(@Nullable Object result) {
        Boolean isSuccess = (Boolean) result;
        if(isSuccess){
            ToastUtils.Companion.showToastSuccess(DetalheParcelaDebitoActivity.this, getString(R.string.mensagem_sucesso));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                onBackPressed();
                finish();
            }

        }else{
            ToastUtils.Companion.showToastError(this, getString(R.string.mensagem_erro));
        }
    }
}
