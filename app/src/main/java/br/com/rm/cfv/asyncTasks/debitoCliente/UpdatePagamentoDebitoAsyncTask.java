package br.com.rm.cfv.asyncTasks.debitoCliente;

import android.os.AsyncTask;
import android.util.Log;

import br.com.rm.cfv.asyncTasks.IPostExecuteInsertAndUpdate;
import br.com.rm.cfv.database.daos.interfaces.PagamentoDebitoDAO;
import br.com.rm.cfv.database.entities.PagamentoDebito;

public class UpdatePagamentoDebitoAsyncTask extends AsyncTask<PagamentoDebito, PagamentoDebito, Boolean> {

    private PagamentoDebitoDAO dao;

    private IPostExecuteInsertAndUpdate iPostExecuteInsertAndUpdate;

    public UpdatePagamentoDebitoAsyncTask(PagamentoDebitoDAO dao, IPostExecuteInsertAndUpdate iPostExecuteInsertAndUpdate) {
        this.dao = dao;
        this.iPostExecuteInsertAndUpdate = iPostExecuteInsertAndUpdate;
    }

    @Override
    protected Boolean doInBackground(PagamentoDebito... pagamentoDebitos) {

        try {
            if (pagamentoDebitos.length > 0) {

                PagamentoDebito pagamentoDebito = pagamentoDebitos[0];
                PagamentoDebito novoDebito = pagamentoDebito.verificaDebito();

                dao.update(pagamentoDebito);

                if (novoDebito != null) {
                    dao.insertAll(novoDebito);
                }

            }

            return true;
        }catch (Exception e){
            Log.e("UpdatePagamentoDebito", e.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        iPostExecuteInsertAndUpdate.afterUpdate(aBoolean);
    }
}
