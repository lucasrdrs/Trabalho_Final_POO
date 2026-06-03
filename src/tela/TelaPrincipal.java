package tela;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import dao.DespesaDAO;
import modelo.Despesa;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.util.List;

public class TelaPrincipal extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable tabelaDespesas;
	private JLabel gastosTotal;
	private JTextField txtFiltro;

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> { 
			try {
				TelaPrincipal frame = new TelaPrincipal();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public TelaPrincipal() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		setResizable(false);
		
		this.addWindowFocusListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowGainedFocus(java.awt.event.WindowEvent e) {
				preencherTabela();
			}
		});
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 40, 764, 480);
		contentPane.add(scrollPane);
		
		tabelaDespesas = new JTable();
		scrollPane.setViewportView(tabelaDespesas);
		
		JLabel lblFiltro = new JLabel("Filtrar por Data: ");
		lblFiltro.setBounds(10, 11, 99, 14);
		contentPane.add(lblFiltro);
		
		txtFiltro = new JTextField();
		txtFiltro.setBounds(101, 8, 279, 20);
		contentPane.add(txtFiltro);
		txtFiltro.setColumns(10);
		
		txtFiltro.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent e) {
				String texto = txtFiltro.getText();
				javax.swing.table.TableRowSorter<DefaultTableModel> sorter = (javax.swing.table.TableRowSorter<DefaultTableModel>) tabelaDespesas.getRowSorter();
				
				if (texto.trim().length() == 0) {
					sorter.setRowFilter(null);
				} else {
					sorter.setRowFilter(javax.swing.RowFilter.regexFilter(texto, 1));
				}
				
				atualizarTotal();
			}
		});
		
		JButton BotaoAdd = new JButton("Adicionar Gastos");
		BotaoAdd.addActionListener(e -> { 
			TelaDespesas telaCadastro = new TelaDespesas();
			telaCadastro.setVisible(true);
		});
		BotaoAdd.setBounds(636, 531, 138, 23);
		contentPane.add(BotaoAdd);
		
		JButton BotaoExcluir = new JButton("Excluir");
		BotaoExcluir.addActionListener(e -> { 
			try {
				int linhaSelecionada = tabelaDespesas.getSelectedRow();

				if (linhaSelecionada == -1) {
					JOptionPane.showMessageDialog(null, "Por favor, selecione um item na tabela para apagar.");
					return;
				}

				int id = (int) tabelaDespesas.getValueAt(linhaSelecionada, 0);

				int resposta = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja apagar?", "Atenção", JOptionPane.YES_NO_OPTION);
				
				if (resposta == JOptionPane.YES_OPTION) {
					DespesaDAO dao = new DespesaDAO();
					dao.excluir(id);

					preencherTabela();
					
					JOptionPane.showMessageDialog(null, "Despesa apagada com sucesso!");
				}

			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null, "Erro ao tentar apagar: " + ex.getMessage());
			}
		});
		BotaoExcluir.setBounds(537, 531, 89, 23);
		contentPane.add(BotaoExcluir);
		
		JButton btEditar = new JButton("Editar");
		btEditar.addActionListener(e -> { 
			try {
				int linhaSelecionada = tabelaDespesas.getSelectedRow();

				if (linhaSelecionada == -1) {
					JOptionPane.showMessageDialog(null, "Por favor, selecione um item na tabela para editar.");
					return; 
				}

				int id = (int) tabelaDespesas.getValueAt(linhaSelecionada, 0);
				String descricao = (String) tabelaDespesas.getValueAt(linhaSelecionada, 2);
				String categoria = (String) tabelaDespesas.getValueAt(linhaSelecionada, 3); 
				double valor = (double) tabelaDespesas.getValueAt(linhaSelecionada, 4);
				String formaPagamento = (String) tabelaDespesas.getValueAt(linhaSelecionada, 5);
				String status = (String) tabelaDespesas.getValueAt(linhaSelecionada, 6);
				boolean foiDebitado = status.equals("Pago"); 
				int parcelas = 1;

				TelaDespesas telaEdicao = new TelaDespesas();
				
				telaEdicao.carregarParaEdicao(id, descricao, valor, categoria, formaPagamento, parcelas, foiDebitado);
				telaEdicao.setVisible(true);

			} catch (Exception ex) {
				System.err.println("Erro ao abrir edição: " + ex.getMessage());
			}
		});
		btEditar.setBounds(438, 531, 89, 23);
		contentPane.add(btEditar);
		
		gastosTotal = new JLabel("Total Gasto: R$ 0,00");
		gastosTotal.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 14));
		gastosTotal.setBounds(10, 533, 300, 23);
		contentPane.add(gastosTotal);
		
		JButton btnPDF = new JButton("Gerar PDF");
		btnPDF.addActionListener(e -> { 
			try {
				String textoTotal = gastosTotal.getText();

				java.text.MessageFormat cabecalho = new java.text.MessageFormat("Relatório de Despesas - Sistema Financeiro");
				java.text.MessageFormat rodape = new java.text.MessageFormat(textoTotal + " | Página {0}");

				boolean impresso = tabelaDespesas.print(JTable.PrintMode.FIT_WIDTH, cabecalho, rodape);

				if (impresso) {
					JOptionPane.showMessageDialog(null, "PDF gerado com sucesso!");
				}

			} catch (java.awt.print.PrinterException ex) {
				JOptionPane.showMessageDialog(null, "Erro ao gerar PDF: " + ex.getMessage());
			}
		});
		btnPDF.setBounds(675, 7, 99, 23);
		contentPane.add(btnPDF);
		
		preencherTabela();
	}
	
	public void atualizarTotal() {
		double total = 0;
		
		for (int i = 0; i < tabelaDespesas.getRowCount(); i++) {
			total += (double) tabelaDespesas.getValueAt(i, 4); 
		}
		
		gastosTotal.setText("Total Gasto: R$ " + String.format("%.2f", total));
	}
	
	private void preencherTabela() {
		try {
			DefaultTableModel modelo = new DefaultTableModel() {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			modelo.addColumn("ID");
			modelo.addColumn("Data");
			modelo.addColumn("Descrição");
			modelo.addColumn("Categoria");
			modelo.addColumn("Valor (R$)");
			modelo.addColumn("Forma de Pgto"); 
			modelo.addColumn("Status");

			DespesaDAO dao = new DespesaDAO();
			List<Despesa> lista = dao.listar();

			for (Despesa d : lista) {
				modelo.addRow(new Object[]{
					d.getId(),
					d.getDataMovimento(), 
					d.getDescricao(),
					d.getTipo().getNome(),
					d.getValor(),
					d.getFormaPagamento(),
					d.isFoiDebitado() ? "Pago" : "Pendente"
				});
			}

			tabelaDespesas.setModel(modelo);
			
			javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(modelo);
			tabelaDespesas.setRowSorter(sorter);

		} catch (Exception e) {
			System.err.println("Erro ao preencher a tabela: " + e.getMessage());
		}
		atualizarTotal();
	}
}