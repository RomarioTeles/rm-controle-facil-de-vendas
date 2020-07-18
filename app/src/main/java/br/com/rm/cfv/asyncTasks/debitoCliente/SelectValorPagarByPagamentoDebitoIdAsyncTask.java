package br.com.rm.cfv.asyncTasks.debitoCliente;

import android.os.AsyncTask;
import android.util.Log;

import br.com.rm.cfv.asyncTasks.IPostExecuteSearch;
import br.com.rm.cfv.database.daos.interfaces.PagamentoDebitoDAO;
import br.com.rm.cfv.database.entities.DebitoCliente;
import br.com.rm.cfv.database.entities.PagamentoDebito;
import br.com.rm.cfv.database.entities.dtos.PagamentoDebitoSubtotalDTO;

public class SelectValorPagarByPagamentoDebitoIdAsyncTask extends AsyncTask<PagamentoDebitoSubtotalDTO, PagamentoDebito, PagamentoDebitoSubtotalDTO> {

    private PagamentoDebitoDAO dao;

    private  IPostExecuteSearch iPostExecuteSearch;

    public SelectValorPagarByPagamentoDebitoIdAsyncTask(PagamentoDebitoDAO dao, IPostExecuteSearch iPostExecuteSearch) {
        this.dao = dao;
        this.iPostExecuteSearch = iPostExecuteSearch;
    }

    @Override
    protected PagamentoDebitoSubtotalDTO doInBackground(PagamentoDebitoSubtotalDTO... debitoClientes) {

        try {
            if (debitoClientes.length > 0) {

                PagamentoDebitoSubtotalDTO pagamentoDebito = debitoClientes[0];

                return dao.getSubtotalByDebitoClienteId(pagamentoDebito.getId());

            }
            return null;
        }catch (Exception e){
            Log.e("UpdatePagamentoDebito", e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(PagamentoDebitoSubtotalDTO result) {
        super.onPostExecute(result);
        iPostExecuteSearch.afterSearch(result);
    }
}
