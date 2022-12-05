package tpi;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class VentanaPrincipal extends JFrame implements ActionListener, ChangeListener {

	private static final long serialVersionUID = 79569113591206658L;
	private JPanel contentPane;
	private JLabel jLabelTituloPrincipal;
	private JButton botonSeleccionarArchivo;
	private JLabel labelArchivoSubido;
	private JButton botonEjecutar;
	private File archivo;
	private JFileChooser fileChooser;
	private String pathDeArchivo;
	private JButton jButtonAvanzar;
	private JScrollPane jScrollPane;
	private JTextArea jTextArea;
	private PlanificadorControlador planificadorControlador;
	private Integer paso;
	private List<Logueo> logueosModoFinalizados;
	private List<Logueo> logueosModoArribados;
	private List<Logueo> logueosModoDefault;
    private JRadioButton jRadioButtonModoFinalizados, jRadioButtonModoArribados,jRadioButtonModoTodosLosInstantes, jRadioButtonModoDefault;
    private ButtonGroup buttonGroupModo;
    private String modoDeEjecucion;
    private JButton jButtonLimpiar;
    private JLabel jLabelElegirModoDeEjecucion;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaPrincipal frame = new VentanaPrincipal();
					frame.setVisible(true);
					frame.setResizable(false);
					frame.jRadioButtonModoDefault.setSelected(true);
					frame.modoDeEjecucion = "DEFAULT";
					ToolTipManager.sharedInstance().setInitialDelay(50);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public VentanaPrincipal() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(50, 50, 1024, 768);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(contentPane);
		this.contentPane.setLayout(null);
		
		this.setTitle("UTN FRRE - Sistemas Operativos 2022 - Trabajo Practico Integrador");
		
		jLabelTituloPrincipal = new JLabel("Simulador de asignacion de memoria y planificacion de procesos");
		jLabelTituloPrincipal.setFont(new Font("SansSerif", Font.PLAIN, 20));
		jLabelTituloPrincipal.setBounds(213, 0, 606, 33);
		contentPane.add(jLabelTituloPrincipal);
		
		botonSeleccionarArchivo = new JButton("Seleccionar Archivo");
		botonSeleccionarArchivo.setToolTipText("Permite seleccionar un archivo .csv que contiene el dataset usado por el simulador.");
		botonSeleccionarArchivo.setFont(new Font("SansSerif", Font.PLAIN, 13));
		botonSeleccionarArchivo.setBounds(24, 47, 161, 23);
		botonSeleccionarArchivo.addActionListener(this);
		contentPane.add(botonSeleccionarArchivo);
		
		fileChooser = new JFileChooser(".\\tests");
		
		labelArchivoSubido = new JLabel("Archivo no seleccionado...");
		labelArchivoSubido.setFont(new Font("SansSerif", Font.PLAIN, 13));
		labelArchivoSubido.setBounds(203, 49, 263, 19);
		contentPane.add(labelArchivoSubido);
		
		botonEjecutar = new JButton("Ejecutar");
		botonEjecutar.setToolTipText("Ejecutar el simulador");
		botonEjecutar.setFont(new Font("SansSerif", Font.PLAIN, 13));
		botonEjecutar.setBounds(24, 109, 96, 23);
		botonEjecutar.addActionListener(this);
		contentPane.add(botonEjecutar);
		
		jButtonLimpiar = new JButton("Limpiar");
		jButtonLimpiar.setToolTipText("Limpiar el Area de Texto");
		jButtonLimpiar.setFont(new Font("SansSerif", Font.PLAIN, 13));
		jButtonLimpiar.setBounds(178, 173, 85, 23);
		jButtonLimpiar.addActionListener(this);
		contentPane.add(jButtonLimpiar);		
		
		jButtonAvanzar = new JButton("Avanzar Instante");
		jButtonAvanzar.setToolTipText("Avanzar al siguiente instante relevante del modo de ejecucion elegido");
		jButtonAvanzar.setFont(new Font("SansSerif", Font.PLAIN, 13));
		jButtonAvanzar.setBounds(23, 172, 145, 23);
		jButtonAvanzar.addActionListener(this);
		contentPane.add(jButtonAvanzar);
		
		jTextArea = new JTextArea();
		jTextArea.setEditable(false);
		
		jScrollPane = new JScrollPane(jTextArea);
		jScrollPane.setBounds(29, 237, 975, 488);
		contentPane.add(jScrollPane);
		
		buttonGroupModo = new ButtonGroup();
		
		jRadioButtonModoDefault = new JRadioButton("Mostrar instantes con procesos arribados o que finalizadon");
		jRadioButtonModoDefault.setLocation(492, 78);
		jRadioButtonModoDefault.setSize(392, 20);
		jRadioButtonModoDefault.setFont(new Font("SansSerif", Font.PLAIN, 13));
		jRadioButtonModoDefault.addChangeListener(this);
//		jRadioButtonModoDefault.setToolTipText("");
		contentPane.add(jRadioButtonModoDefault);
		buttonGroupModo.add(jRadioButtonModoDefault);
		
		jRadioButtonModoArribados = new JRadioButton("Mostrar instantes con procesos que arribaron");
		jRadioButtonModoArribados.setLocation(492, 122);
		jRadioButtonModoArribados.setSize(321, 20);
		jRadioButtonModoArribados.setFont(new Font("SansSerif", Font.PLAIN, 13));
		jRadioButtonModoArribados.addChangeListener(this);
//		jRadioButtonModoArribados.setToolTipText("");
		contentPane.add(jRadioButtonModoArribados);
		buttonGroupModo.add(jRadioButtonModoArribados);

		
		jRadioButtonModoFinalizados = new JRadioButton("Mostrar instantes con procesos que finalizaron");
		jRadioButtonModoFinalizados.setLocation(492, 100);
		jRadioButtonModoFinalizados.setSize(321, 20);
		jRadioButtonModoFinalizados.setFont(new Font("SansSerif", Font.PLAIN, 13));
		jRadioButtonModoFinalizados.addChangeListener(this);
//		jRadioButtonModoFinalizados.setToolTipText("");
		contentPane.add(jRadioButtonModoFinalizados);
		buttonGroupModo.add(jRadioButtonModoFinalizados);
		
		jRadioButtonModoTodosLosInstantes = new JRadioButton("Mostar todos los instantes");
		jRadioButtonModoTodosLosInstantes.setLocation(492, 144);
		jRadioButtonModoTodosLosInstantes.setSize(200, 20);
		jRadioButtonModoTodosLosInstantes.setFont(new Font("SansSerif", Font.PLAIN, 13));
		jRadioButtonModoTodosLosInstantes.addChangeListener(this);
//		jRadioButtonModoTodosLosInstantes.setToolTipText("");
		contentPane.add(jRadioButtonModoTodosLosInstantes);
		buttonGroupModo.add(jRadioButtonModoTodosLosInstantes);
		
		jLabelElegirModoDeEjecucion = new JLabel("Elegir modo de ejecucion: ");
		jLabelElegirModoDeEjecucion.setFont(new Font("SansSerif", Font.PLAIN, 13));
		jLabelElegirModoDeEjecucion.setBounds(492, 53, 263, 19);
		contentPane.add(jLabelElegirModoDeEjecucion);


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
			this.jTextArea.setText("");		
			this.paso = 0;
			
			//Declara e instancia un objeto de la clase PlanificadorControlador
			planificadorControlador = new PlanificadorControlador();
			planificadorControlador.ejecutar(this.pathDeArchivo);
			
			logueosModoFinalizados = new ArrayList<Logueo>();
			logueosModoArribados = new ArrayList<Logueo>();
			logueosModoDefault = new ArrayList<Logueo>();
			for (Logueo logueo : planificadorControlador.getLogueos()) {
				if(logueo.getEsInstanteDondeArribanProcesos() || logueo.getEsInstanteConProcesosTerminados())
					logueosModoDefault.add(logueo);
				if(logueo.getEsInstanteDondeArribanProcesos())
					logueosModoArribados.add(logueo);
				if (logueo.getEsInstanteConProcesosTerminados())
					logueosModoFinalizados.add(logueo);
			}
			
			if (this.planificadorControlador.getLogueoInicial() != null) {
				this.jTextArea.setText(planificadorControlador.getLogueoInicial().getTexto());				
			}
		}
		
		if (evento.getSource() == jButtonAvanzar) {
			
			if (this.modoDeEjecucion.equals("DEFAULT") && this.paso < logueosModoDefault.size()) {
				String texto = logueosModoDefault.get(paso).getTexto();
				this.jTextArea.setText(this.jTextArea.getText().concat(texto));
				this.paso++;
			}
			
			if (this.modoDeEjecucion.equals("DEFAULT") && this.paso == logueosModoDefault.size()) {
				String texto = this.planificadorControlador.getLogueoFinal().getTexto();
				this.jTextArea.setText(this.jTextArea.getText().concat(texto));
				this.paso++;
			}
			
			if (this.modoDeEjecucion.equals("DEFAULT") && this.paso > logueosModoDefault.size()) {
				JOptionPane.showMessageDialog(null, "Ya se han mostrado todos los instantes del modo elegido", "Aviso",
						JOptionPane.WARNING_MESSAGE);
			}
			
			if (this.modoDeEjecucion.equals("ARRIBADOS") && this.paso < logueosModoArribados.size()) {
				String texto = logueosModoArribados.get(paso).getTexto();
				this.jTextArea.setText(this.jTextArea.getText().concat(texto));
				this.paso++;
			}
			
			if (this.modoDeEjecucion.equals("ARRIBADOS") && this.paso == logueosModoArribados.size()) {
				String texto = this.planificadorControlador.getLogueoFinal().getTexto();
				this.jTextArea.setText(this.jTextArea.getText().concat(texto));
				this.paso++;
			}
			
			if (this.modoDeEjecucion.equals("ARRIBADOS") && this.paso > logueosModoArribados.size()) {
				JOptionPane.showMessageDialog(null, "Ya se han mostrado todos los instantes del modo elegido", "Aviso",
						JOptionPane.WARNING_MESSAGE);
			}
			
			if (this.modoDeEjecucion.equals("FINALIZADOS") && this.paso < logueosModoFinalizados.size()) {
				String texto = logueosModoFinalizados.get(paso).getTexto();
				this.jTextArea.setText(this.jTextArea.getText().concat(texto));
				this.paso++;
			}
			
			if (this.modoDeEjecucion.equals("FINALIZADOS") && this.paso == logueosModoFinalizados.size()) {
				String texto = this.planificadorControlador.getLogueoFinal().getTexto();
				this.jTextArea.setText(this.jTextArea.getText().concat(texto));
				this.paso++;
			}
			
			if (this.modoDeEjecucion.equals("FINALIZADOS") && this.paso > logueosModoFinalizados.size()) {
				JOptionPane.showMessageDialog(null, "Ya se han mostrado todos los instantes del modo elegido", "Aviso",
						JOptionPane.WARNING_MESSAGE);
			}
			
			if (this.modoDeEjecucion.equals("TODOS_LOS_INSTANTES") && this.paso < planificadorControlador.getLogueos().size()) {
				String texto = planificadorControlador.getLogueos().get(paso).getTexto();
				this.jTextArea.setText(this.jTextArea.getText().concat(texto));
				this.paso++;
			}
			
			if (this.modoDeEjecucion.equals("TODOS_LOS_INSTANTES") && this.paso == planificadorControlador.getLogueos().size()) {
				String texto = this.planificadorControlador.getLogueoFinal().getTexto();
				this.jTextArea.setText(this.jTextArea.getText().concat(texto));
				this.paso++;
			}
			
			if (this.modoDeEjecucion.equals("TODOS_LOS_INSTANTES") && this.paso > planificadorControlador.getLogueos().size()) {
				JOptionPane.showMessageDialog(null, "Ya se han mostrado todos los instantes del modo elegido", "Aviso",
						JOptionPane.WARNING_MESSAGE);
			}
		}
		
		if(evento.getSource() == jButtonLimpiar) {
			this.jTextArea.setText("");	
		}
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {

		if (this.jRadioButtonModoDefault.isSelected()) {
			this.modoDeEjecucion = "DEFAULT";
			this.paso = 0;
		}
		
		if (this.jRadioButtonModoArribados.isSelected()) {
			this.modoDeEjecucion = "ARRIBADOS";
			this.paso = 0;
		}
		
		if (this.jRadioButtonModoFinalizados.isSelected()) {
			this.modoDeEjecucion = "FINALIZADOS";
			this.paso = 0;
		}
		
		if (this.jRadioButtonModoTodosLosInstantes.isSelected()) {
			this.modoDeEjecucion = "TODOS_LOS_INSTANTES";
			this.paso = 0;
		}
		
	}
}
