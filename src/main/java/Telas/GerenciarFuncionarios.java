package Telas;

import DAO.UsuarioDao;
import Model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GerenciarFuncionarios extends JFrame {
    private JPanel main;
    private JTable table1;
    private JButton editarButton;
    private JTable table2;
    private JButton aprovarButton;

    private Usuario usuarioLogado;
    private DefaultTableModel modeloTable1;
    private DefaultTableModel modeloTable2;

    public GerenciarFuncionarios(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        setContentPane(main);
        setTitle("Gerenciar Funcionários");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        configurarTabelas();
        carregarTabelas();

        aprovarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int linhaSelecionada = table2.getSelectedRow();
                if (linhaSelecionada != -1) {
                    Long idAlvo = (Long) modeloTable2.getValueAt(linhaSelecionada, 0);
                    String nomeFuncionario = (String) modeloTable2.getValueAt(linhaSelecionada, 2);

                    int confirmacao = JOptionPane.showConfirmDialog(main,
                            "Deseja aprovar o cadastro de " + nomeFuncionario + "?",
                            "Confirmar Aprovação",
                            JOptionPane.YES_NO_OPTION);

                    if (confirmacao == JOptionPane.YES_OPTION) {
                        UsuarioDao dao = new UsuarioDao();
                        boolean sucesso = dao.changeState(1, idAlvo, usuarioLogado.getIdUsuario());

                        if (sucesso) {
                            JOptionPane.showMessageDialog(main, "Funcionário aprovado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                            carregarTabelas();
                        } else {
                            JOptionPane.showMessageDialog(main, "Erro ao aprovar o funcionário.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(main, "Selecione um funcionário pendente para aprovar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        editarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int linhaSelecionada = table1.getSelectedRow();
                if (linhaSelecionada != -1) {
                    Long idAlvo = (Long) modeloTable1.getValueAt(linhaSelecionada, 0);
                    UsuarioDao dao = new UsuarioDao();
                    Optional<Usuario> usuarioEditar = dao.buscarPorId(idAlvo);

                    if (usuarioEditar.isPresent()) {
                        new FuncionariosCadastro(Optional.of(usuarioLogado), usuarioEditar);
                    }
                } else {
                    JOptionPane.showMessageDialog(main, "Selecione um funcionário para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        setVisible(true);
    }

    private void configurarTabelas() {
        String[] colunasTable1 = {"ID", "Crachá", "Nome", "E-mail", "Cargo", "Estado"};
        modeloTable1 = new DefaultTableModel(colunasTable1, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table1.setModel(modeloTable1);

        String[] colunasTable2 = {"ID", "Crachá", "Nome", "E-mail"};
        modeloTable2 = new DefaultTableModel(colunasTable2, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table2.setModel(modeloTable2);
    }

    private void carregarTabelas() {
        UsuarioDao dao = new UsuarioDao();
        List<Usuario> todosUsuarios = dao.listarUsuarios();

        modeloTable1.setRowCount(0);
        modeloTable2.setRowCount(0);

        List<Usuario> usuariosGerenciamento = todosUsuarios.stream()
                .filter(u -> u.getEstado() != 2)
                .sorted((u1, u2) -> Integer.compare(u2.getEstado(), u1.getEstado()))
                .collect(Collectors.toList());

        for (Usuario u : usuariosGerenciamento) {
            String cargoStr = u.getCargo() == 0 ? "Administrador" : "Funcionário";
            String estadoStr = u.getEstado() == 1 ? "Ativo" : "Desativado";

            Object[] linha = {
                    u.getIdUsuario(),
                    u.getNumIdentificacao(),
                    u.getNomeUsuario(),
                    u.getEmail(),
                    cargoStr,
                    estadoStr
            };
            modeloTable1.addRow(linha);
        }

        List<Usuario> usuariosPendentes = todosUsuarios.stream()
                .filter(u -> u.getEstado() == 2)
                .collect(Collectors.toList());

        for (Usuario u : usuariosPendentes) {
            Object[] linha = {
                    u.getIdUsuario(),
                    u.getNumIdentificacao(),
                    u.getNomeUsuario(),
                    u.getEmail()
            };
            modeloTable2.addRow(linha);
        }
    }
}