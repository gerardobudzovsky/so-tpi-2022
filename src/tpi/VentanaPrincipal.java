package tpi;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class VentanaPrincipal extends JFrame implements ActionListener {

	private static final long serialVersionUID = 79569113591206658L;
	private JPanel contentPane;
	private JButton botonSeleccionarArchivo;
	private JLabel labelArchivoSubido;
	private JButton botonEjecutar;
	private File archivo;
	private JFileChooser fileChooser;
	private String pathDeArchivo;
	private JButton jButtonAvanzarModoDeAUno;
	private JScrollPane jScrollPane;
	private JTextArea jTextArea;
	private PlanificadorControlador planificadorControlador;
	private Integer paso;
	private JButton jButtonAvanzarModoTerminados;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaPrincipal frame = new VentanaPrincipal();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public VentanaPrincipal() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 799, 601);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.setTitle("UTN FRRE - Sistemas Operativos 2022 - Trabajo Integrador");
		
		botonSeleccionarArchivo = new JButton("Seleccionar Archivo");
		botonSeleccionarArchivo.setToolTipText("Permite seleccionar un archivo el cual contendrá el dataset usado por el algoritmo.");
		botonSeleccionarArchivo.setFont(new Font("SansSerif", Font.PLAIN, 13));
		botonSeleccionarArchivo.setBounds(23, 89, 161, 23);
		botonSeleccionarArchivo.addActionListener(this);
		contentPane.add(botonSeleccionarArchivo);
		
		fileChooser = new JFileChooser(".\\tests");
		
		labelArchivoSubido = new JLabel("Archivo no seleccionado...");
		labelArchivoSubido.setFont(new Font("SansSerif", Font.PLAIN, 13));
		labelArchivoSubido.setBounds(221, 91, 263, 19);
		contentPane.add(labelArchivoSubido);
		
		botonEjecutar = new JButton("Ejecutar");
		botonEjecutar.setToolTipText("");
		botonEjecutar.setFont(new Font("SansSerif", Font.PLAIN, 13));
		botonEjecutar.setBounds(23, 135, 96, 23);
		botonEjecutar.addActionListener(this);
		contentPane.add(botonEjecutar);
		
		JLabel lblSimuladorDeAsignacion = new JLabel("Simulador de asignacion de memoria y planificacion de procesos");
		lblSimuladorDeAsignacion.setFont(new Font("SansSerif", Font.PLAIN, 20));
		lblSimuladorDeAsignacion.setBounds(10, 24, 590, 49);
		contentPane.add(lblSimuladorDeAsignacion);
		
		jButtonAvanzarModoDeAUno = new JButton("Avanzar en Modo de a Uno");
		jButtonAvanzarModoDeAUno.setToolTipText("");
		jButtonAvanzarModoDeAUno.setFont(new Font("SansSerif", Font.PLAIN, 13));
		jButtonAvanzarModoDeAUno.setBounds(221, 191, 183, 21);
		jButtonAvanzarModoDeAUno.addActionListener(this);
		contentPane.add(jButtonAvanzarModoDeAUno);
		
		jTextArea = new JTextArea();
		jTextArea.setEditable(false);
		
		jScrollPane = new JScrollPane(jTextArea);
		jScrollPane.setBounds(29, 237, 746, 303);
		contentPane.add(jScrollPane);
		
		jButtonAvanzarModoTerminados = new JButton("Avanzar en Modo Procesos Terminados");
		jButtonAvanzarModoTerminados.setToolTipText("");
		jButtonAvanzarModoTerminados.setFont(new Font("SansSerif", Font.PLAIN, 13));
		jButtonAvanzarModoTerminados.addActionListener(this);
		jButtonAvanzarModoTerminados.setBounds(436, 191, 85, 21);
		contentPane.add(jButtonAvanzarModoTerminados);
		
	}

	@Override
	public void actionPerformed(ActionEvent evento) {

		
		if (evento.getSource() == botonSeleccionarArchivo) {
			int returnVal = fileChooser.showOpenDialog(VentanaPrincipal.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				archivo = fileChooser.getSelectedFile();
				pathDeArchivo = this.archivo.getAbsolutePath();
				this.labelArchivoSubido.setText(archivo.getName());
			}
		}
		
		if (evento.getSource() == botonEjecutar) {
			//Declara e instancia un objeto de la clase PlanificadorControlador
			planificadorControlador = new PlanificadorControlador();
			planificadorControlador.ejecutar(this.pathDeArchivo);
			paso = 0;
		}
		
		if (evento.getSource() == jButtonAvanzarModoDeAUno) {
			String texto = planificadorControlador.getLogueos().get(paso).getTexto();
			this.jTextArea.setText(this.jTextArea.getText().concat(texto));
			paso ++;
		}
		
		if (evento.getSource() == jButtonAvanzarModoTerminados) {
			String texto = planificadorControlador.getLogueos().get(paso).getTexto();
			this.jTextArea.setText(this.jTextArea.getText().concat(texto));
			paso ++;
		}
	}
}
