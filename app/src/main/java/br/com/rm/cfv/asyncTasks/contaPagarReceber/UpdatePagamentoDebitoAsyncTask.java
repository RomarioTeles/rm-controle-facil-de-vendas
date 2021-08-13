package br.com.rm.cfv.asyncTasks.contaPagarReceber;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate;
import br.com.rm.cfv.constants.TipoItemBalancete;
import br.com.rm.cfv.database.daos.AppDataBase;
import br.com.rm.cfv.database.entities.Balancete;
import br.com.rm.cfv.database.entities.ContaPagarReceber;
import br.com.rm.cfv.database.entities.ItemBalancete;
import br.com.rm.cfv.database.entities.PagamentoDebito;

public class UpdatePagamentoDebitoAsyncTask extends AsyncTask<PagamentoDebito, PagamentoDebito, Boolean> {

    private final AppDataBase dao;

    private final IPostExecuteInsertAndUpdate iPostExecuteInsertAndUpdate;

    public UpdatePagamentoDebitoAsyncTask(AppDataBase dao, IPostExecuteInsertAndUpdate iPostExecuteInsertAndUpdate) {
        this.dao = dao;
        this.iPostExecuteInsertAndUpdate = iPostExecuteInsertAndUpdate;
    }

    @Override
    protected Boolean doInBackground(PagamentoDebito... pagamentoDebitos) {

        try {
            if (pagamentoDebitos.length > 0) {

                PagamentoDebito pagamentoDebito = pagamentoDebitos[0];
                PagamentoDebito novoDebito = pagamentoDebito.verificaDebito();

                dao.pagamentoDebitoDAO().update(pagamentoDebito);

                criaBalancete(pagamentoDebito);

                if (novoDebito != null) {
                    dao.pagamentoDebitoDAO().insertAll(novoDebito);

                }

            }

            return true;
        }catch (Exception e){
            Log.e("UpdatePagamentoDebito", e.getMessage());
            return false;
        }
    }

    private void criaBalancete( PagamentoDebito pagamentoDebito){

        Calendar data = Calendar.getInstance();
        int mes = data.get(Calendar.MONTH) + 1;
        int ano = data.get(Calendar.YEAR);
        Balancete balancete =  dao.balanceteDAO().findByMesAndAno(mes, ano);
        long balanceteId;
        if(balancete.getUid() == null){
            balanceteId = dao.balanceteDAO().insert(new Balancete(null, mes, ano ));
        }else{
            balanceteId = balancete.getUid().longValue();
        }

        ContaPagarReceber conta = dao.contaPagarReceberDAO().findById(pagamentoDebito.getContaPagarReceberId());

        ItemBalancete itemBalanco = new ItemBalancete(
                null, new Date(),
                TipoItemBalancete.Companion.getByTipoReferencia(conta.getTipoRef()),
                pagamentoDebito.getValorPago(),
                conta.getNomeRef(),
                conta.getIdRef(),
                (int) balanceteId
        );

        dao.itemBalanceteDAO().insertAll(itemBalanco);

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        iPostExecuteInsertAndUpdate.afterUpdate(aBoolean);
    }
}
