package dao;

import conexao.ConexaoBanco;
import modelo.Despesa;
import modelo.TipoMovimento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DespesaDAO {

    public void salvar(Despesa despesa) {
        String sql = "INSERT INTO despesa (data_movimento, id_tipo_movimento, descricao, valor, forma_pagamento, quantidade_parcelas, data_vencimento, foi_debitado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexaoBanco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setDate(1, new java.sql.Date(despesa.getDataMovimento().getTime())); 
            stmt.setInt(2, despesa.getTipo().getId()); 
            stmt.setString(3, despesa.getDescricao());
            stmt.setDouble(4, despesa.getValor());
            stmt.setString(5, despesa.getFormaPagamento());
            stmt.setInt(6, despesa.getQuantidadeParcelas());
            stmt.setNull(7, java.sql.Types.DATE);
            stmt.setBoolean(8, despesa.isFoiDebitado());

            stmt.executeUpdate();
            System.out.println("Despesa salva com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao salvar a despesa: " + e.getMessage());
        }
    }
    
    public List<Despesa> listar() {
        List<Despesa> listaDespesas = new ArrayList<>();
        
        String sql = "SELECT d.id, d.data_movimento, d.descricao, d.valor, d.forma_pagamento, " +
                "d.quantidade_parcelas, d.foi_debitado, " +
                "t.id AS id_tipo, t.nome AS tipo_nome " +
                "FROM despesa d " +
                "JOIN tipo_movimento t ON d.id_tipo_movimento = t.id " +
                "ORDER BY d.data_movimento ASC";

        try (Connection con = ConexaoBanco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet r = stmt.executeQuery()) {

            while (r.next()) {
                Despesa despesa = new Despesa();
                
                despesa.setId(r.getInt("id"));
                despesa.setDataMovimento(r.getDate("data_movimento"));
                despesa.setDescricao(r.getString("descricao"));
                despesa.setValor(r.getDouble("valor"));
                despesa.setFormaPagamento(r.getString("forma_pagamento"));
                despesa.setQuantidadeParcelas(r.getInt("quantidade_parcelas"));
                despesa.setFoiDebitado(r.getBoolean("foi_debitado"));

                TipoMovimento tipo = new TipoMovimento();
                tipo.setId(r.getInt("id_tipo"));
                tipo.setNome(r.getString("tipo_nome"));
                
                despesa.setTipo(tipo);

                listaDespesas.add(despesa);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar as despesas: " + e.getMessage());
        }

        return listaDespesas;
    }
    
    public void excluir(int id) {
        String sql = "DELETE FROM despesa WHERE id = ?";

        try (Connection con = ConexaoBanco.conectar();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Erro ao excluir a despesa: " + e.getMessage());
        }
    }
    
    public void atualizar(Despesa despesa) {
        String sql = "UPDATE despesa SET descricao = ?, valor = ?, id_tipo_movimento = ?, forma_pagamento = ?, quantidade_parcelas = ?, foi_debitado = ? WHERE id = ?";

        try (java.sql.Connection con = conexao.ConexaoBanco.conectar();
             java.sql.PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, despesa.getDescricao());
            stmt.setDouble(2, despesa.getValor());
            stmt.setInt(3, despesa.getTipo().getId());
            stmt.setString(4, despesa.getFormaPagamento());
            stmt.setInt(5, despesa.getQuantidadeParcelas());
            stmt.setBoolean(6, despesa.isFoiDebitado());
            stmt.setInt(7, despesa.getId()); 

            stmt.executeUpdate();

        } catch (java.sql.SQLException e) {
            System.err.println("Erro ao atualizar a despesa: " + e.getMessage());
        }
    }
}