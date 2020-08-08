package br.com.rm.cfv.activities.contaPagarReceber.contaPagarReceberViewer.detalhe;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

import br.com.rm.cfv.R;
import br.com.rm.cfv.activities.BaseActivity;
import br.com.rm.cfv.adapters.MeioPagamentoAdapter;
import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate;
import br.com.rm.cfv.asyncTasks.contaPagarReceber.UpdatePagamentoDebitoAsyncTask;
import br.com.rm.cfv.database.entities.PagamentoDebito;
import br.com.rm.cfv.utils.ToastUtils;
import br.com.rm.dateutils.DateFormatUtils;
import br.com.rm.numberUtils.DecimalFormatUtils;

public class DetalheParcelaActivity extends BaseActivity implements IPostExecuteInsertAndUpdate {

    private MeioPagamentoAdapter meioPagamentoAdapter;


    private Spinner listViewMeioPagamento;

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
            textViewVencimento.setText(DateFormatUtils.format(pagamentoDebito.getDataVencimento(), "dd/MM/yyyy"));

            TextView textViewValor = findViewById(R.id.textViewValor);
            textViewValor.setText(getString(R.string.currency_format,DecimalFormatUtils.decimalFormatPtBR(pagamentoDebito.getValor())));

            SwitchCompat switchCompat = findViewById(R.id.switchPagarTotal);

            final TextInputEditText textInputEditValorPagar = findViewById(R.id.textInputEditValorPagar);
            textInputEditValorPagar.setText(String.valueOf(pagamentoDebito.getValorPago()));

            if(pagamentoDebito.getValor() <= pagamentoDebito.getValorPago()){
                switchCompat.setEnabled(false);
                switchCompat.setChecked(true);
            }else {
                switchCompat.setChecked(false);
                switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        textInputEditValorPagar.setText(String.valueOf(pagamentoDebito.getValor()));
                });
            }

            listViewMeioPagamento = findViewById(R.id.listViewMeioPagamento);
            meioPagamentoAdapter = new MeioPagamentoAdapter(this, true);
            listViewMeioPagamento.setAdapter(meioPagamentoAdapter);


            listViewMeioPagamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    pagamentoDebito.setMeioPagamento(meioPagamentoAdapter.getItem(position).name());
                    meioPagamentoAdapter.setSelected(meioPagamentoAdapter.getItem(position));
                    meioPagamentoAdapter.notifyDataSetChanged();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            fab().setOnClickListener(v -> {
                if(!textInputEditValorPagar.getText().toString().isEmpty()) {
                    pagamentoDebito.setValorPago(Double.valueOf(textInputEditValorPagar.getText().toString()));
                    new UpdatePagamentoDebitoAsyncTask(
                            getCfvApplication().getDataBase(),
                            DetalheParcelaActivity.this).execute(pagamentoDebito);
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
            ToastUtils.Companion.showToastSuccess(DetalheParcelaActivity.this, getString(R.string.mensagem_sucesso));
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
