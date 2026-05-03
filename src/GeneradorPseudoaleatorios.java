import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GeneradorPseudoaleatorios extends JFrame {

    // --- ESTILO ---
    private static final Color AZUL = new Color(41, 98, 175);
    private static final Color AZUL2 = new Color(235, 241, 255);
    private static final Font F_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font F_PLAIN = new Font("Segoe UI", Font.PLAIN, 13);

    

    // --- HOME ---
    private JPanel cards;
    private CardLayout cardLayout;

    // --- COMPONENTES GENERADOR ---
    private JComboBox<String> combo;
    private JPanel panelParams;
    private JLabel lblError;
    private DefaultTableModel tableModel;
    private JTextField fN, fX0, fX1, fD, fA, fB, fC, fM, fK, fG, fSeeds;

    // --- COMPONENTES COMPROBACIÓN ---
    private JComboBox<String> comboPruebas;
    private JTextField fDatosPrueba, fAlfa;
    private JTextArea areaResultados;

    public GeneradorPseudoaleatorios() {
        super("Sistemas de Simulación");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 850);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        cards.add(crearMenuPrincipal(), "MENU");
        cards.add(crearPanelGenerador(), "GENERADOR");
        cards.add(crearPanelComprobacion(), "COMPROBACION");

        add(cards);
        setVisible(true);
    }

    private JPanel crearMenuPrincipal() {
        JPanel menu = new JPanel(new GridBagLayout());
        menu.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        JLabel title = new JLabel("Simulador de Números Pseudoaleatorios", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(AZUL);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        menu.add(title, gbc);

        JButton btnGen = crearBotonMenu("GENERADOR", "Algoritmos de Generación");
        btnGen.addActionListener(e -> cardLayout.show(cards, "GENERADOR"));

        JButton btnComp = crearBotonMenu("COMPROBACIÓN", "Pruebas Estadísticas");
        btnComp.addActionListener(e -> cardLayout.show(cards, "COMPROBACION"));

        gbc.gridy = 1; gbc.gridwidth = 1;
        menu.add(btnGen, gbc);
        gbc.gridx = 1;
        menu.add(btnComp, gbc);

        return menu;
    }

    private JPanel crearPanelGenerador() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AZUL);
        header.setBorder(new EmptyBorder(5, 10, 5, 10)); // Un poco más estrecho

        
        JPanel panelIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelIzquierdo.setOpaque(false);
        
        JButton btnVolver = new JButton("⬅ Volver");
        btnVolver.addActionListener(e -> cardLayout.show(cards, "MENU"));
        
        JLabel logoIzquierdo = new JLabel(escalarImagen("src/logos/ISC.png"));
        
        panelIzquierdo.add(btnVolver);
        panelIzquierdo.add(logoIzquierdo);
        header.add(panelIzquierdo, BorderLayout.WEST); 

    
        JLabel lblTitle = new JLabel("Generador de Números", SwingConstants.CENTER);
        lblTitle.setFont(F_BOLD); lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle, BorderLayout.CENTER);


        JLabel logoDerecho = new JLabel(escalarImagen("src/logos/Logo-ITSH.png"));
        header.add(logoDerecho, BorderLayout.EAST);

    

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(15, 20, 15, 20));
        center.setBackground(Color.WHITE);

        center.add(label("Algoritmo:"));
        combo = new JComboBox<>(new String[]{
            "1 – Cuadrados Medios", "2– Productos Medios", "3– Multiplicador Constante",
            "4 – Congruencial Lineal", "5 – Congruencial Multiplicativo", "6 – Congruencial Aditivo",
            "7 – Congruencial Cuadrático", "8 – Blum, Blum y Shub"
        });
        combo.addActionListener(e -> refreshParams());
        center.add(combo);
        center.add(Box.createVerticalStrut(10));

        center.add(label("Cantidad de números (n):"));
        fN = field(); center.add(fN);
        center.add(Box.createVerticalStrut(10));

        panelParams = new JPanel();
        panelParams.setBackground(Color.WHITE);
        panelParams.setLayout(new BoxLayout(panelParams, BoxLayout.Y_AXIS));
        center.add(panelParams);

        lblError = new JLabel(" ");
        lblError.setForeground(Color.RED);
        center.add(lblError);

        JButton btnExec = new JButton(" -> GENERAR NÚMEROS ");
        btnExec.setBackground(AZUL); btnExec.setForeground(Color.WHITE);
        btnExec.addActionListener(e -> generar());
        center.add(btnExec);
        center.add(Box.createVerticalStrut(10));

        tableModel = new DefaultTableModel(new String[]{"Iteración", "Valor Xᵢ", "Número rᵢ"}, 0);
        JTable tabla = new JTable(tableModel);
        center.add(new JScrollPane(tabla));

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        
        refreshParams();
        return root;
    }

    private JPanel crearPanelComprobacion() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(60, 60, 60));
        header.setBorder(new EmptyBorder(5, 10, 5, 10));

        
        JPanel panelIzquierdoComp = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelIzquierdoComp.setOpaque(false);

        JButton btnVolver = new JButton("⬅ Volver");
        btnVolver.addActionListener(e -> cardLayout.show(cards, "MENU"));
        
        JLabel logoIzquierdoComp = new JLabel(escalarImagen("ISC.png"));
        
        panelIzquierdoComp.add(btnVolver);
        panelIzquierdoComp.add(logoIzquierdoComp);
        header.add(panelIzquierdoComp, BorderLayout.WEST);

       
        JLabel lblTitle = new JLabel("Pruebas Estadísticas", SwingConstants.CENTER);
        lblTitle.setFont(F_BOLD); lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle, BorderLayout.CENTER);

        
        JLabel logoDerechoComp = new JLabel(escalarImagen("Logo-ITSH.png"));
        header.add(logoDerechoComp, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH); 
        
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(20, 20, 20, 20));
        center.setBackground(Color.WHITE);
        
        center.add(label("1. Ingrese los números ri (separados por coma):"));
        fDatosPrueba = field();
        fDatosPrueba.setToolTipText("Ejemplo: 0.1234, 0.5678, 0.9101");
        center.add(fDatosPrueba);
        center.add(Box.createVerticalStrut(15));

        // NUEVO: Nivel de significancia Alfa
        center.add(label("2. Nivel de Significancia (Alfa, ej. 0.05):"));
        fAlfa = field();
        fAlfa.setText("0.05"); // Valor por defecto
        center.add(fAlfa);
        center.add(Box.createVerticalStrut(15));

        center.add(label("3. Seleccione la Prueba:"));
        String[] pruebas = {
            "1. Prueba de Medias",
            "2. Prueba de Varianza",
            "3. Chi-Cuadrada",
            "4. Kolmogorov-Smirnov",
            "5. Corridas Arriba y Abajo"
        };
        comboPruebas = new JComboBox<>(pruebas);
        center.add(comboPruebas);
        center.add(Box.createVerticalStrut(15));

        JButton btnCalcular = new JButton("Calcular Prueba Estadística");
        btnCalcular.setBackground(AZUL); btnCalcular.setForeground(Color.black);
        btnCalcular.setFont(F_BOLD);
        btnCalcular.addActionListener(e -> ejecutarPrueba());
        center.add(btnCalcular);
        center.add(Box.createVerticalStrut(15));

        center.add(label("Resultados y Operaciones:"));
        areaResultados = new JTextArea(15, 40);
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaResultados.setBackground(new Color(245, 245, 245));
        center.add(new JScrollPane(areaResultados));

        root.add(center, BorderLayout.CENTER);
        return root;
    }

    // --- PRUEBAS ESTADÍSTICAS ---
    private void ejecutarPrueba() {
        try {
            String input = fDatosPrueba.getText().trim();
            if (input.isEmpty()) throw new Exception("Debe ingresar datos.");
            
            double alfa = Double.parseDouble(fAlfa.getText().trim());
            if (alfa <= 0 || alfa >= 1) throw new Exception("Alfa debe estar entre 0 y 1 (ej. 0.05).");

            String[] partes = input.split(",");
            double[] ri = new double[partes.length];
            for (int i = 0; i < partes.length; i++) {
                ri[i] = Double.parseDouble(partes[i].trim());
            }

            int index = comboPruebas.getSelectedIndex();
            switch (index) {
                case 0 -> pruebaDeMedias(ri, alfa);
                case 1 -> pruebaDeVarianza(ri, alfa);
                case 2 -> pruebaChiCuadrada(ri, alfa);
                case 3 -> pruebaKolmogorov(ri, alfa);
                case 4 -> pruebaCorridasArribaAbajo(ri, alfa);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Asegúrese de ingresar números válidos en Alfa y en los datos.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void pruebaCorridasArribaAbajo(double[] ri, double alfa) {
        int n = ri.length;
        if (n < 2) {
            areaResultados.setText("Error: Se requieren al menos 2 datos para esta prueba.");
            return;
        }

        // 1. Generar secuencia S de unos y ceros
        int[] s = new int[n - 1];
        StringBuilder seqStr = new StringBuilder("{");
        for (int i = 1; i < n; i++) {
            s[i - 1] = (ri[i] > ri[i - 1]) ? 1 : 0;
            seqStr.append(s[i - 1]).append(i == n - 1 ? "" : ",");
        }
        seqStr.append("}");

        // 2. Determinar número de corridas observadas Co
        int Co = 1;
        for (int i = 1; i < s.length; i++) {
            if (s[i] != s[i - 1]) {
                Co++;
            }
        }

        // 3. Cálculos estadísticos
        double mu = (2.0 * n - 1) / 3.0;
        double varianza = (16.0 * n - 29) / 90.0;
        double desviacion = Math.sqrt(varianza);
        double Z0 = Math.abs((Co - mu) / desviacion);
        
        double ZAlpha2 = obtenerZDosColas(alfa);

        StringBuilder sb = new StringBuilder();
        sb.append("--- PRUEBA DE CORRIDAS ARRIBA Y ABAJO ---\n");
        sb.append("Nivel de significancia (α): ").append(alfa).append("\n");
        sb.append("Cantidad de datos (n): ").append(n).append("\n");
        sb.append("Secuencia S (n-1): ").append(seqStr).append("\n\n");
        
        sb.append("ESTADÍSTICOS:\n");
        sb.append("Corridas observadas (Co): ").append(Co).append("\n");
        sb.append("Valor esperado (μCo): ").append(String.format("%.4f", mu)).append("\n");
        sb.append("Varianza (σ²Co): ").append(String.format("%.4f", varianza)).append("\n");
        sb.append("Z calculado (Z₀): ").append(String.format("%.4f", Z0)).append("\n");
        sb.append("Z crítico (Zα/2): ").append(ZAlpha2).append("\n\n");

        sb.append("EVALUACIÓN:\n");
        sb.append("¿ ").append(String.format("%.4f", Z0)).append(" < ").append(ZAlpha2).append(" ?\n\n");

        if (Z0 < ZAlpha2) {
            sb.append(">>> RESULTADO: SE ACEPTA H0 <<<\n");
            sb.append("No se puede rechazar la independencia de los números.\n");
            sb.append("Conclusión: Los números son independientes.");
        } else {
            sb.append(">>> RESULTADO: SE RECHAZA H0 <<<\n");
            sb.append("El estadístico Z₀ es mayor al valor crítico.\n");
            sb.append("Conclusión: Los números no son independientes.");
        }

        areaResultados.setText(sb.toString());
    }

    
    
    // Obtiene el valor Z de la normal estándar para dos colas (aproximaciones comunes)
    private double obtenerZDosColas(double alfa) {
        if (alfa == 0.01) return 2.576;
        if (alfa == 0.10) return 1.645;
        return 1.96; // Por defecto alfa=0.05
    }

    // Obtiene el valor Z de la normal estándar para una cola
    private double obtenerZUnaCola(double alfa) {
        if (alfa == 0.01) return 2.33;
        if (alfa == 0.10) return 1.28;
        return 1.645; // Por defecto alfa=0.05
    }

    // Aproximación  para Chi-Cuadrada
    private double calcularChiCuadradaInversa(double gradosLibertad, double z) {
        double term = 1.0 - (2.0 / (9.0 * gradosLibertad)) + (z * Math.sqrt(2.0 / (9.0 * gradosLibertad)));
        return gradosLibertad * Math.pow(term, 3);
    }

    // --- IMPLEMENTACIÓN DE PRUEBAS ---

    private void pruebaDeMedias(double[] ri, double alfa) {
        int n = ri.length;
        double suma = 0;
        for (double val : ri) suma += val;

        double promedio = suma / n;
        double zAlpha2 = obtenerZDosColas(alfa); 
        double errorEstandar = 1.0 / Math.sqrt(12.0 * n);

        double LI = 0.5 - (zAlpha2 * errorEstandar);
        double LS = 0.5 + (zAlpha2 * errorEstandar);

        StringBuilder sb = new StringBuilder();
        sb.append("--- PRUEBA DE MEDIAS ---\n");
        sb.append("Nivel de significancia (α): ").append(alfa).append("\n");
        sb.append("Datos (n): ").append(n).append("\n");
        sb.append("Promedio (r̄): ").append(String.format("%.6f", promedio)).append("\n\n");

        sb.append("VALORES CRÍTICOS:\n");
        sb.append("Z(α/2): ").append(zAlpha2).append("\n");
        sb.append("Límite Inferior (LI): ").append(String.format("%.6f", LI)).append("\n");
        sb.append("Límite Superior (LS): ").append(String.format("%.6f", LS)).append("\n\n");

        sb.append("EVALUACIÓN:\n");
        sb.append("¿ ").append(String.format("%.4f", LI)).append(" ≤ ")
          .append(String.format("%.4f", promedio)).append(" ≤ ")
          .append(String.format("%.4f", LS)).append(" ?\n\n");

        if (promedio >= LI && promedio <= LS) {
            sb.append(">>> RESULTADO: SE ACEPTA H0 <<<\n");
            sb.append("El promedio se encuentra dentro de los límites.\n");
            sb.append("Conclusión: Los números tienen un valor esperado de 0.5.");
        } else {
            sb.append(">>> RESULTADO: SE RECHAZA H0 <<<\n");
            sb.append("El promedio está fuera de los límites.\n");
            sb.append("Conclusión: Los números NO tienen un valor esperado de 0.5.");
        }

        areaResultados.setText(sb.toString());
    }

    private void pruebaDeVarianza(double[] ri, double alfa) {
        int n = ri.length;
        double suma = 0;
        for (double val : ri) suma += val;
        double promedio = suma / n;

        double sumaCuadrados = 0;
        for (double val : ri) sumaCuadrados += Math.pow(val - promedio, 2);
        
        double varianza = sumaCuadrados / (n - 1);
        double chi0 = (varianza * (n - 1)) / (1.0/12.0);
        double gl = n - 1;

        // Calculamos Chi-cuadrada
        double zAlfa2 = obtenerZDosColas(alfa);
        double chiSup = calcularChiCuadradaInversa(gl, zAlfa2); // Cola derecha (+Z)
        double chiInf = calcularChiCuadradaInversa(gl, -zAlfa2); // Cola izquierda (-Z)

        // Límites de la varianza esperada
        double LI = chiInf / (12.0 * gl);
        double LS = chiSup / (12.0 * gl);

        StringBuilder sb = new StringBuilder();
        sb.append("--- PRUEBA DE VARIANZA ---\n");
        sb.append("Nivel de significancia (α): ").append(alfa).append("\n");
        sb.append("n = ").append(n).append(" | GL = ").append((int)gl).append("\n");
        sb.append("Varianza (S²): ").append(String.format("%.6f", varianza)).append("\n");
        sb.append("Estadístico χ²₀: ").append(String.format("%.4f", chi0)).append("\n\n");

        sb.append("VALORES CRÍTICOS (Chi-Cuadrada aproximada):\n");
        sb.append("χ²(inf): ").append(String.format("%.4f", chiInf)).append("\n");
        sb.append("χ²(sup): ").append(String.format("%.4f", chiSup)).append("\n\n");

        sb.append("LÍMITES DE ACEPTACIÓN PARA LA VARIANZA:\n");
        sb.append("LI: ").append(String.format("%.6f", LI)).append("\n");
        sb.append("LS: ").append(String.format("%.6f", LS)).append("\n\n");

        sb.append("EVALUACIÓN:\n");
        sb.append("¿ ").append(String.format("%.6f", LI)).append(" ≤ ")
          .append(String.format("%.6f", varianza)).append(" ≤ ")
          .append(String.format("%.6f", LS)).append(" ?\n\n");

        if (varianza >= LI && varianza <= LS) {
            sb.append(">>> RESULTADO: SE ACEPTA H0 <<<\n");
            sb.append("La varianza está dentro del rango permitido.\n");
            sb.append("Conclusión: Los números tienen una varianza de 1/12.");
        } else {
            sb.append(">>> RESULTADO: SE RECHAZA H0 <<<\n");
            sb.append("La varianza está fuera del rango permitido.\n");
            sb.append("Conclusión: Los números NO tienen una varianza de 1/12.");
        }
        areaResultados.setText(sb.toString());
    }

    private void pruebaChiCuadrada(double[] ri, double alfa) {
        int n = ri.length;
        int m = (int) Math.sqrt(n);
        double rango = 1.0 / m;
        int[] O = new int[m];

        for (double num : ri) {
            int index = (int) (num / rango);
            if (index == m) index = m - 1;
            O[index]++;
        }

        double E = (double) n / m;
        double chi0 = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("--- PRUEBA CHI-CUADRADA (UNIFORMIDAD) ---\n");
        sb.append("Nivel de significancia (α): ").append(alfa).append("\n");
        sb.append("n = ").append(n).append("\n");
        sb.append("Intervalos (m) = ").append(m).append("\n");
        sb.append("Frecuencia esperada (E) = ").append(String.format("%.4f", E)).append("\n\n");
        sb.append("Intervalo\tO\tE\t(O-E)²/E\n");

        for (int i = 0; i < m; i++) {
            double li = i * rango;
            double ls = li + rango;
            double valor = Math.pow(O[i] - E, 2) / E;
            chi0 += valor;
            sb.append(String.format("[%.2f - %.2f)\t%d\t%.2f\t%.4f\n", li, ls, O[i], E, valor));
        }

        double gl = m - 1;
        double zAlfa = obtenerZUnaCola(alfa); // Chi cuadrada de uniformidad es de una cola (derecha)
        double chiTabla = calcularChiCuadradaInversa(gl, zAlfa);

        sb.append("\nESTADÍSTICOS:\n");
        sb.append("χ² Calculada (χ²₀): ").append(String.format("%.4f", chi0)).append("\n");
        sb.append("Grados de Libertad: ").append((int)gl).append("\n");
        sb.append("χ² Crítica (Tabla): ").append(String.format("%.4f", chiTabla)).append("\n\n");

        sb.append("EVALUACIÓN:\n");
        sb.append("¿ ").append(String.format("%.4f", chi0)).append(" ≤ ").append(String.format("%.4f", chiTabla)).append(" ?\n\n");

        if (chi0 <= chiTabla) {
            sb.append(">>> RESULTADO: SE ACEPTA H0 <<<\n");
            sb.append("El estadístico es menor al valor crítico.\n");
            sb.append("Conclusión: Los números provienen de una distribución uniforme.");
        } else {
            sb.append(">>> RESULTADO: SE RECHAZA H0 <<<\n");
            sb.append("El estadístico supera al valor crítico.\n");
            sb.append("Conclusión: Los números NO se distribuyen uniformemente.");
        }
        areaResultados.setText(sb.toString());
    }

    private void pruebaKolmogorov(double[] ri, double alfa) {
        int n = ri.length;
        Arrays.sort(ri);
        double Dmas = 0, Dmenos = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("--- PRUEBA KOLMOGOROV-SMIRNOV ---\n");
        sb.append("Nivel de significancia (α): ").append(alfa).append("\n");
        sb.append("n = ").append(n).append("\n\n");
        sb.append("i\t ri\t i/n\t (i-1)/n\t D+\t D-\n");

        for (int i = 0; i < n; i++) {
            double r = ri[i];
            double i_n = (double)(i + 1) / n;
            double i1_n = (double)(i) / n;

            double dMas = i_n - r;
            double dMenos = r - i1_n;

            if (dMas > Dmas) Dmas = dMas;
            if (dMenos > Dmenos) Dmenos = dMenos;

            sb.append(String.format("%d\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\n", i+1, r, i_n, i1_n, dMas, dMenos));
        }

        double D = Math.max(Dmas, Dmenos);
        
        // Cálculo de D crítico utilizando la aproximación estándar de la tabla KS
        double constKS;
        if (alfa == 0.01) constKS = 1.63;
        else if (alfa == 0.10) constKS = 1.22;
        else constKS = 1.36; // Default alfa 0.05
        
        double Dtabla = constKS / Math.sqrt(n);

        sb.append("\nESTADÍSTICOS:\n");
        sb.append("D+ máximo = ").append(String.format("%.4f", Dmas)).append("\n");
        sb.append("D- máximo = ").append(String.format("%.4f", Dmenos)).append("\n");
        sb.append("D calculada = ").append(String.format("%.4f", D)).append("\n");
        sb.append("D crítica (Tabla) = ").append(String.format("%.4f", Dtabla)).append("\n\n");

        sb.append("EVALUACIÓN:\n");
        sb.append("¿ ").append(String.format("%.4f", D)).append(" ≤ ").append(String.format("%.4f", Dtabla)).append(" ?\n\n");

        if (D <= Dtabla) {
            sb.append(">>> RESULTADO: SE ACEPTA H0 <<<\n");
            sb.append("La diferencia máxima es menor al límite crítico.\n");
            sb.append("Conclusión: Los números provienen de una distribución uniforme.");
        } else {
            sb.append(">>> RESULTADO: SE RECHAZA H0 <<<\n");
            sb.append("La diferencia máxima excede el límite crítico.\n");
            sb.append("Conclusión: Los números NO se distribuyen uniformemente.");
        }
        areaResultados.setText(sb.toString());
    }

    // --- LÓGICA DE GENERADORES ---
    private void generar() {
        lblError.setText(" ");
        tableModel.setRowCount(0);
        try {
            int n = posInt(fN, "n");
            int metodo = combo.getSelectedIndex();
            List<double[]> res = switch (metodo) {
                case 0 -> cuadradosMedios(n);
                case 1 -> productosMedios(n);
                case 2 -> multiplicadorConstante(n);
                case 3 -> congruencialLineal(n);
                case 4 -> congruencialMultiplicativo(n);
                case 5 -> congruencialAditivo(n);
                case 6 -> congruencialCuadratico(n);
                case 7 -> blumBlumShub(n);
                default -> null;
            };
            if (res != null) {
                for (double[] f : res) {
                    long Xi = (long) f[1];
                    String ri = (metodo <= 2) ? "0." + String.format("%04d", Xi) : String.format("%.4f", f[2]);
                    tableModel.addRow(new Object[]{(int) f[0], Xi, ri});
                }
            }
        } catch (Exception ex) {
            lblError.setText("Error: " + ex.getMessage());
        }
    }

    private List<double[]> cuadradosMedios(int n) {
        long xn = posLong(fX0, "X₀"); List<double[]> res = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            long c = xn * xn; String s = String.format("%08d", c);
            xn = Long.parseLong(s.substring(2, 6)); res.add(new double[]{i, (double)xn, xn / 10000.0});
        }
        return res;
    }
    
    private List<double[]> productosMedios(int n) {
        long X0 = posLong(fX0,"X₀"), X1 = posLong(fX1,"X₁"); int D = posInt(fD,"D");
        List<double[]> r = new ArrayList<>(); long prev = X0, curr = X1;
        for (int i = 1; i <= n; i++) {
            String sY = String.format("%0" + (2*D) + "d", prev * curr);
            int s = (sY.length() - D) / 2; long next = Long.parseLong(sY.substring(s, s + D));
            r.add(new double[]{i, (double)next, next / Math.pow(10, D)}); prev = curr; curr = next;
        }
        return r;
    }

    private List<double[]> multiplicadorConstante(int n) {
        long Xi = posLong(fX0,"X₀"), a = posLong(fA,"a"); int D = posInt(fD,"D");
        List<double[]> r = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            String sY = String.format("%0" + (2*D) + "d", a * Xi);
            int s = (sY.length() - D) / 2; Xi = Long.parseLong(sY.substring(s, s + D));
            r.add(new double[]{i, (double)Xi, Xi / Math.pow(10, D)});
        }
        return r;
    }

    private List<double[]> congruencialLineal(int n) {
        long X = posLong(fX0,"X₀"), a = posLong(fA,"a"), c = posLong(fC,"c"), m = posLong(fM,"m");
        List<double[]> r = new ArrayList<>();
        for (int i = 1; i <= n; i++) { X = (a * X + c) % m; r.add(new double[]{i, (double)X, (double) X / (m - 1)}); }
        return r;
    }

    private List<double[]> congruencialMultiplicativo(int n) {
        long X = posLong(fX0, "X₀"), k = posLong(fK, "k"), g = posLong(fG, "g");
        long a = 3 + 8 * k, m = (long) Math.pow(2, g); List<double[]> r = new ArrayList<>();
        for (int i = 1; i <= n; i++) { X = (a * X) % m; r.add(new double[]{i, (double)X, (double) X / (m - 1)}); }
        return r;
    }

    private List<double[]> congruencialAditivo(int n) {
        long m = posLong(fM,"m"); String[] partes = fSeeds.getText().split(",");
        List<Long> seq = new ArrayList<>(); for (String p : partes) seq.add(Long.parseLong(p.trim()));
        int ns = seq.size(); List<double[]> r = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            long next = (seq.get(seq.size()-1) + seq.get(seq.size()-ns)) % m;
            seq.add(next); r.add(new double[]{i+1, (double)next, (double) next / (m - 1)});
        }
        return r;
    }

    private List<double[]> congruencialCuadratico(int n) {
        long X = posLong(fX0,"X₀"), m = posLong(fM,"m"), a = posLong(fA,"a"), b = posLong(fB,"b"), c = posLong(fC,"c");
        List<double[]> r = new ArrayList<>();
        for (int i = 1; i <= n; i++) { X = (a*X*X + b*X + c) % m; r.add(new double[]{i, (double)X, (double) X / (m - 1)}); }
        return r;
    }

    private List<double[]> blumBlumShub(int n) {
        long X = posLong(fX0,"X₀"), m = posLong(fM,"m"); List<double[]> r = new ArrayList<>();
        for (int i = 1; i <= n; i++) { X = (X*X) % m; r.add(new double[]{i, (double)X, (double) X / (m - 1)}); }
        return r;
    }

    // --- UTILS UI ---
    private void refreshParams() {
        if (panelParams == null) return;
        panelParams.removeAll();
        fX0=field(); fX1=field(); fD=field(); fA=field(); fB=field(); fC=field(); fM=field(); fK=field(); fG=field(); fSeeds=field();
        int sel = combo.getSelectedIndex();
        switch (sel) {
            case 0 -> row("Semilla X₀:", fX0);
            case 1 -> { row("X₀:", fX0); row("X₁:", fX1); row("D:", fD); }
            case 2 -> { row("X₀:", fX0); row("a:", fA); row("D:", fD); }
            case 3 -> { row("X₀:", fX0); row("a:", fA); row("c:", fC); row("m:", fM); }
            case 4 -> { row("X₀:", fX0); row("k:", fK); row("g:", fG); }
            case 5 -> { row("m:", fM); row("Semillas (coma):", fSeeds); }
            case 6 -> { row("X₀:", fX0); row("m:", fM); row("a:", fA); row("b:", fB); row("c:", fC); }
            case 7 -> { row("X₀:", fX0); row("m:", fM); }
        }
        panelParams.revalidate(); panelParams.repaint();
    }

    private JLabel label(String t) { JLabel l = new JLabel(t); l.setFont(F_BOLD); return l; }
    private JTextField field() { 
        JTextField tf = new JTextField(); 
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); 
        tf.setPreferredSize(new Dimension(200, 30));
        return tf; 
    }
    private void row(String lbl, JTextField tf) { panelParams.add(label(lbl)); panelParams.add(tf); }
    private int posInt(JTextField tf, String n) { return Integer.parseInt(tf.getText().trim()); }
    private long posLong(JTextField tf, String n) { return Long.parseLong(tf.getText().trim()); }

    private JButton crearBotonMenu(String texto, String sub) {
        JButton b = new JButton("<html><center><b>" + texto + "</b><br>" + sub + "</center></html>");
        b.setPreferredSize(new Dimension(200, 80));
        b.setBackground(AZUL2); b.setForeground(AZUL);
        return b;
    }


    private ImageIcon escalarImagen(String nombreArchivo) {
        try {
            
            java.net.URL imgURL = getClass().getResource("/logos/" + nombreArchivo);
            if (imgURL != null) {
                ImageIcon icono = new ImageIcon(imgURL);
                Image img = icono.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            } else {
                System.err.println("No se encontró el archivo: " + nombreArchivo);
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(GeneradorPseudoaleatorios::new);
    }
}  