package tela;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import dao.DespesaDAO;
import modelo.Despesa;
import modelo.TipoMovimento;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;

public class TelaDespesas extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtDescricao;
	private JTextField txtValor;
	private JComboBox<String> cbCategoria;
	private JComboBox<String> cbFormaPagamento;
	private JSpinner txtParcelas;
	private JCheckBox chkDebitado;
	
	private int idDaDespesa = 0;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TelaDespesas frame = new TelaDespesas();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public TelaDespesas() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 439, 284);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblDescricao = new JLabel("Descrição:");
		lblDescricao.setBounds(10, 24, 63, 14);
		contentPane.add(lblDescricao);
		
		txtDescricao = new JTextField();
		txtDescricao.setBounds(72, 21, 341, 20);
		contentPane.add(txtDescricao);
		txtDescricao.setColumns(10);
		
		JLabel lblValor = new JLabel("Valor (R$):");
		lblValor.setBounds(10, 57, 63, 14);
		contentPane.add(lblValor);
		
		txtValor = new JTextField();
		txtValor.setBounds(72, 54, 341, 20);
		contentPane.add(txtValor);
		txtValor.setColumns(10);
		
		JLabel lblCategoria = new JLabel("Categoria:");
		lblCategoria.setBounds(10, 89, 80, 14);
		contentPane.add(lblCategoria);
		
		String[] categorias = {"Alimentação", "Transporte", "Lazer", "Outros"};
		cbCategoria = new JComboBox<>(categorias);
		cbCategoria.setBounds(72, 85, 313, 22);
		contentPane.add(cbCategoria);
		
		cbFormaPagamento = new JComboBox<>();
		cbFormaPagamento.setModel(new DefaultComboBoxModel<>(new String[] {"Dinheiro", "PIX", "Cartão de Crédito", "Cartão de Débito ", "Boleto"}));
		cbFormaPagamento.setBounds(138, 118, 165, 22);
		contentPane.add(cbFormaPagamento);
		
		txtParcelas = new JSpinner();
		txtParcelas.setModel(new SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
		txtParcelas.setToolTipText("");
		txtParcelas.setBounds(383, 119, 30, 20);
		contentPane.add(txtParcelas);
		
		chkDebitado = new JCheckBox("Conta já foi paga/debitada");
		chkDebitado.setBounds(72, 147, 175, 23);
		contentPane.add(chkDebitado);
		
		JLabel lblNewLabel = new JLabel("Forma de Pagamento:");
		lblNewLabel.setBounds(10, 122, 124, 14);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Nº Parcelas:");
		lblNewLabel_1.setBounds(310, 122, 75, 14);
		contentPane.add(lblNewLabel_1);
		
		JButton btnSalvar = new JButton("Salvar");
		btnSalvar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String descricao = txtDescricao.getText();
					
					String valorTexto = txtValor.getText().replace(",", "."); 
					double valor = Double.parseDouble(valorTexto);

					Despesa despesa = new Despesa();
					despesa.setDescricao(descricao);
					despesa.setValor(valor);
					despesa.setDataMovimento(new java.util.Date());
					
					String formaPagamento = cbFormaPagamento.getSelectedItem().toString();
					despesa.setFormaPagamento(formaPagamento);

					int parcelas = (int) txtParcelas.getValue();
					despesa.setQuantidadeParcelas(parcelas);

					boolean debitado = chkDebitado.isSelected();
					despesa.setFoiDebitado(debitado);
					
					TipoMovimento tipo = new TipoMovimento();
					
					String categoriaSelecionada = cbCategoria.getSelectedItem().toString();
					int idCategoria = 1;
					
					if (categoriaSelecionada.equals("Alimentação")) {
						idCategoria = 1; 
					} else if (categoriaSelecionada.equals("Transporte")) {
						idCategoria = 2; 
					} else if (categoriaSelecionada.equals("Lazer")) {
						idCategoria = 3; 
					} else if (categoriaSelecionada.equals("Outros")) {
						idCategoria = 4; 
					}
					
					tipo.setId(idCategoria);
					despesa.setTipo(tipo);
					
					DespesaDAO dao = new DespesaDAO();

					if (idDaDespesa == 0) {
						dao.salvar(despesa);
					} else {
						despesa.setId(idDaDespesa);
						dao.atualizar(despesa);
					}

					JOptionPane.showMessageDialog(null, "Despesa salva com sucesso!");
					
					dispose();

				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Erro ao tentar salvar: " + ex.getMessage());        
				}
			}
		});
		btnSalvar.setBounds(324, 211, 89, 23);
		contentPane.add(btnSalvar);
	}
	
	public void carregarParaEdicao(int id, String descricao, double valor, String categoria, String formaPagamento, int parcelas, boolean foiDebitado) {
		this.idDaDespesa = id; 
		txtDescricao.setText(descricao);
		txtValor.setText(String.valueOf(valor));
		cbCategoria.setSelectedItem(categoria);
		cbFormaPagamento.setSelectedItem(formaPagamento);
		txtParcelas.setValue(parcelas);
		chkDebitado.setSelected(foiDebitado);
	}
}