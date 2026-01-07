package com.hotel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 * Gestion de reservas de Hotel.
 */
public class App {

    // --------- CONSTANTES Y VARIABLES GLOBALES ---------

    // Tipos de habitacion
    public static final String TIPUS_ESTANDARD = "Estàndard";
    public static final String TIPUS_SUITE = "Suite";
    public static final String TIPUS_DELUXE = "Deluxe";

    // Servicios adicionales
    public static final String SERVEI_ESMORZAR = "Esmorzar";
    public static final String SERVEI_GIMNAS = "Gimnàs";
    public static final String SERVEI_SPA = "Spa";
    public static final String SERVEI_PISCINA = "Piscina";

    // Capacidad inicial
    public static final int CAPACITAT_ESTANDARD = 30;
    public static final int CAPACITAT_SUITE = 20;
    public static final int CAPACITAT_DELUXE = 10;

    // IVA
    public static final float IVA = 0.21f;

    // Scanner único
    public static Scanner sc = new Scanner(System.in);

    // HashMaps de consulta
    public static HashMap<String, Float> preusHabitacions = new HashMap<String, Float>();
    public static HashMap<String, Integer> capacitatInicial = new HashMap<String, Integer>();
    public static HashMap<String, Float> preusServeis = new HashMap<String, Float>();

    // HashMaps dinàmicos
    public static HashMap<String, Integer> disponibilitatHabitacions = new HashMap<String, Integer>();
    public static HashMap<Integer, ArrayList<String>> reserves = new HashMap<Integer, ArrayList<String>>();

    // Generador de nombres aleatorios para las reservas
    public static Random random = new Random();

    // --------- METODO MAIN ---------

    /**
     * Método principal. Muestra el menú en un bucle y gestiona la opción elegida
     * hasta que el usuario decide salir.
     */
    public static void main(String[] args) {
        inicialitzarPreus();

        int opcio = 0;
        do {
            mostrarMenu();
            opcio = llegirEnter("Seleccione una opció: ");
            gestionarOpcio(opcio);
        } while (opcio != 6);

        System.out.println("Eixint del sistema... Gràcies per utilitzar el gestor de reserves!");
    }

    // --------- METODOS DEMANDADOS ---------

    /**
     * Configura LOS precios de las habitaciones, servicios addicionales y
     * las capacidades iniciales en los HashMaps correspondientes.
     */
    public static void inicialitzarPreus() {
        // Precios habitaciones
        preusHabitacions.put(TIPUS_ESTANDARD, 50f);
        preusHabitacions.put(TIPUS_SUITE, 100f);
        preusHabitacions.put(TIPUS_DELUXE, 150f);

        // Capacidades iniciales
        capacitatInicial.put(TIPUS_ESTANDARD, CAPACITAT_ESTANDARD);
        capacitatInicial.put(TIPUS_SUITE, CAPACITAT_SUITE);
        capacitatInicial.put(TIPUS_DELUXE, CAPACITAT_DELUXE);

        // Disponibilidad inicial (comienza igual que la capacidad)
        disponibilitatHabitacions.put(TIPUS_ESTANDARD, CAPACITAT_ESTANDARD);
        disponibilitatHabitacions.put(TIPUS_SUITE, CAPACITAT_SUITE);
        disponibilitatHabitacions.put(TIPUS_DELUXE, CAPACITAT_DELUXE);

        // Precios servicios
        preusServeis.put(SERVEI_ESMORZAR, 10f);
        preusServeis.put(SERVEI_GIMNAS, 15f);
        preusServeis.put(SERVEI_SPA, 20f);
        preusServeis.put(SERVEI_PISCINA, 25f);
    }

    /**
     * Mostrar el menu principal con las opciones disponibles para el usuario.
     */
    public static void mostrarMenu() {
        System.out.println("\n===== MENÚ PRINCIPAL =====");
        System.out.println("1. Reservar una habitació");
        System.out.println("2. Alliberar una habitació");
        System.out.println("3. Consultar disponibilitat");
        System.out.println("4. Llistar reserves per tipus");
        System.out.println("5. Obtindre una reserva");
        System.out.println("6. Ixir");
    }

    /**
     * Procesar la opcion seleccionada por el usuario y llamar al metodo
     * correspondiente.
     */
    public static void gestionarOpcio(int opcio) {
        // TODO:
        switch (opcio) {
            case 1:
                reservarHabitacio();
                break;
            case 2:
                alliberarHabitacio();
                break;
            case 3:
                consultarDisponibilitat();
                break;
            case 4:
                obtindreReservaPerTipus();
                break;
            case 5:
                obtindreReserva();
                break;
            case 6:
                // No ponemos nada ya que esta solucionado arriba donde inicia el main.
                break;
            default:
                System.out.println("Número introducido no valido.");

        }
    }

    /**
     * Gestiona todos los procesos de reserva: selecciona el tipo de habitacion,
     * servicios adicionales, calcula el precio total y generacion del codigo de
     * reserva.
     */
    public static void reservarHabitacio() {
        System.out.println("\n------ RESERVAR HABITACION ------");

        // Elegir tipo de habitación disponible.

        String tipo = seleccionarTipusHabitacioDisponible();

        if (tipo == null) {
            System.out.println("No se pudo realizar la reserva.");
            return;
        }

        // Servicios adicionales elegidos por el usuario.

        ArrayList<String> servicios = seleccionarServeis();

        // Calcular precio total.

        float precioTotal = calcularPreuTotal(tipo, servicios);

        // Genera codigo de reserva aleatorio.

        int codigo = generarCodiReserva();

        // Guardar la reserva.

        ArrayList<String> datosReserva = new ArrayList<>();
        datosReserva.add(tipo);                // Para un solo guardado.
        datosReserva.addAll(servicios);        // Para guardar todos los servicios.Puede ser ninguno o varios.

        reserves.put(codigo, datosReserva);

        // Reducir la disponibilidad por cada reserva dependiendo del tipo de
        // habitacion.

        int disponibles = disponibilitatHabitacions.get(tipo);
        disponibilitatHabitacions.put(tipo, disponibles - 1);

        // Mostrar resumen solicitud reserva

        System.out.println("\n Reserva realizada.");
        System.out.println("Codigo unico de reserva: " + codigo);
        System.out.println("Tipo de habitación: " + tipo);
        System.out.println("Servicios: " + servicios);
        System.out.println("Precio total (IVA incluido): " + precioTotal + " €");
    }

    /**
     * Pregunta al usuario el tipo de habitacion en formato numerico y
     * devuelve el nombre del tipo de habitacion.
     */
    public static String seleccionarTipusHabitacio() {

        // Estructura Switch con la diferencia de que al elegir retorna a
        // la constante tipo de habitación.
        // TODO:
        System.out.println("\n------ TIPO DE HABITACION ------");
        System.out.println("¿Que tipo de habitacion quiere reservar?");
        System.out.println("1. Estandar");
        System.out.println("2. Suite");
        System.out.println("3. Deluxe");
        int tipo = llegirEnter("Selecciona una opción: ");

        switch (tipo) {
            case 1:
                return TIPUS_ESTANDARD;
            case 2:
                return TIPUS_SUITE;
            case 3:
                return TIPUS_DELUXE;
            default:
                System.out.println("Opción no valida.");
                return null;
        }
    }

    /**
     * Muestra la disponibilidad y el precio de cada tipo de habitacion,
     * solicita al usuario un tipo y solo devuelkve si todavia hay habiotaciones
     * disponibles. En caso contrario, devuelve null.
     */
    public static String seleccionarTipusHabitacioDisponible() {
        System.out.println("\n------ DISPONIBILIDAD DE HABITACIONES ------");
        /*
         * Se llama al metodo para ver tipos de habitacion y su disponibilidad, se
         * encuentra en la parte baja del codigo
         */
        // TODO:

        mostrarInfoTipus(TIPUS_ESTANDARD);
        mostrarInfoTipus(TIPUS_SUITE);
        mostrarInfoTipus(TIPUS_DELUXE);

        String tipo = seleccionarTipusHabitacio();

        // Comprobamos si el tipo es válido
        if (tipo == null) {
            System.out.println("Debe elegir uno de los 3 tipos de habitación.");
            return null;
        }

        // Comprobamos si hay habitaciones disponibles
        if (disponibilitatHabitacions.get(tipo) > 0) {
            return tipo;
        } else {
            System.out.println("No quedan habitaciones disponibles de ese tipo.");
            return null;
        }
    }

    /**
     * Permite elegir servicios adicionales (entre 0 y 4, sin repeticion) y
     * los devuelve en un ArrayList de String.
     */
    public static ArrayList<String> seleccionarServeis() {
        // TODO:
        /*
         * Array que contiene servicio adicionales con do / while para que se repita
         * mientras usuario quiera servicios
         */

        ArrayList<String> serviciosSeleccionados = new ArrayList<>();
        int opcion;

        do {
            System.out.println("\n Puede usted contratar servicios adicionales: ");
            System.out.println("1. Almorzar");
            System.out.println("2. Gimnasio");
            System.out.println("3. Spa");
            System.out.println("4. Piscina");
            System.out.println("5. No quiero mas servicios");

            opcion = llegirEnter("Seleccione una opción: ");

            switch (opcion) {
                /*
                 * Se utiliza contains para comprobar si la opcion elegida ya se solicito, sino
                 * lo solicito se añade a cada servicio.
                 */

                case 1:
                    if (!serviciosSeleccionados.contains(SERVEI_ESMORZAR)) {
                        serviciosSeleccionados.add(SERVEI_ESMORZAR);
                        System.out.println("Servicio almorzar se añadio.");
                    } else {
                        System.out.println("Ese servicio ya esta añadido.");
                    }
                    break;

                case 2:
                    if (!serviciosSeleccionados.contains(SERVEI_GIMNAS)) {
                        serviciosSeleccionados.add(SERVEI_GIMNAS);
                        System.out.println("Servicio Gimnasio se añadio.");
                    } else {
                        System.out.println("Ese servicio ya esta añadido.");
                    }
                    break;

                case 3:
                    if (!serviciosSeleccionados.contains(SERVEI_SPA)) {
                        serviciosSeleccionados.add(SERVEI_SPA);
                        System.out.println("Servicio Spa se añadio.");
                    } else {
                        System.out.println("Ese servicio ya esta añadido.");
                    }
                    break;

                case 4:
                    if (!serviciosSeleccionados.contains(SERVEI_PISCINA)) {
                        serviciosSeleccionados.add(SERVEI_PISCINA);
                        System.out.println("Servicio Piscina se añadio.");
                    } else {
                        System.out.println("Ese servicio ya está añadido.");
                    }
                    break;
                /* Salir del bucle por parte del usuario y queda guardada su eleccion. */
                case 5:
                    System.out.println("Selección de servicios finalizada.");
                    break;

                default:
                    System.out.println("Opción no valida, debe elegir entre 1 y el 5.");
            }

        } while (opcion != 5);

        return serviciosSeleccionados;
    }

    /**
     * Calcula y devuelve el coste total de la reserva, incluida la habitacion,
     * los servicios seleccionados y el IVA.
     */
    public static float calcularPreuTotal(String tiposHabitacion, ArrayList<String> serviciosSeleccionados) {
        // TODO:
        /* for-each para recorrer los servicios seleccionados y sumarlos. */

        float total = preusHabitacions.get(tiposHabitacion);
        for (String servicio : serviciosSeleccionados) {
            total = total + preusServeis.get(servicio);
        }
        total = total + (total * IVA);

        return total;
    }

    /**
     * Genera y deveuelve el codigo unico de reserva de tres cifras
     * (entre 100 y 999) que no se pueda repetir.
     */
    public static int generarCodiReserva() {
        // TODO:
        int codigo;
        do {
            /*
             * Crea codigo random (n-1) +100 en este caso 899 + 100, luego comprueba que no
             * este ya guardado en la variable codigo, si es no lo guarda, si es si repite
             * creacion de numero random.
             */

            codigo = random.nextInt(900) + 100;
        } while (reserves.containsKey(codigo));

        return codigo;
    }

    /**
     * Permite liberar una habitacion utlizando el codigo de reserva
     * y actualiza la disponibilidad.
     */
    public static void alliberarHabitacio() {
        System.out.println("\n====== LIBERAR HABITACIÓN ======");
        // TODO: Demanar codi, tornar habitació i eliminar reserva

        int codigo = llegirEnter("Introduzca el codigo de su reserva: ");

        // Comprobar que el codigo existe.
        if (!reserves.containsKey(codigo)) {
            System.out.println("No existe ninguna reserva con ese codigo.");
            return;
        }
        // Obtener datos reserva.
        ArrayList<String> datosReserva = reserves.get(codigo);

        // Tipo de habitacion.
        String tiposHabitacion = datosReserva.get(0);

        // Aumentamos la disponibilidad del tipo de habitación.
        int disponibles = disponibilitatHabitacions.get(tiposHabitacion);
        disponibilitatHabitacions.put(tiposHabitacion, disponibles + 1);

        // Eliminamos la reserva.
        reserves.remove(codigo);

        System.out.println("Habitacion liberada correctamente.");
    }

    /**
     * Muestra la disponibilidad actual de las habitaciones(libres y ocupadas).
     */
    public static void consultarDisponibilitat() {
        // TODO: Mostrar lliures i ocupades

        // Imprime en pantalla tipo de habitaciones separado con \t que es tabulacion
        // que no como queda pero se utiliza abajo en el codigo.

        System.out.println("\n------ DISPONIBILIDAD DE HABITACIONES ------");
        System.out.println("Tipo \t \t Libres \t Ocupadas");
        mostrarDisponibilitatTipus(TIPUS_ESTANDARD);
        mostrarDisponibilitatTipus(TIPUS_SUITE);
        mostrarDisponibilitatTipus(TIPUS_DELUXE);
    }

    /**
     * Funcin recursiva. Muestra los datos de todas las reservas
     * asociadas a un tipo de habitacion.
     */
    public static void llistarReservesPerTipus(int[] codis, String tipus) {
        // TODO: Implementar recursivitat
    }

    /**
     * Permite consultar los detalles de una reserva introduciendo el codigo.
     */
    public static void obtindreReserva() {
        System.out.println("\n===== CONSULTAR RESERVA =====");
        // TODO: Mostrar dades d'una reserva concreta

        // variable codigo para guardar lo que introduce el usuario.

        int codigo = llegirEnter("Introduce el código de la reserva: ");

        // Comprobar si existe.
        if (!reserves.containsKey(codigo)) {
            System.out.println("No existe ninguna reserva con ese código.");
            return;
        }

        // Obtener los datos de la reserva.
        ArrayList<String> datosReserva = reserves.get(codigo);

        // Mostrar la información, tipo sera siempre 0 por la estructura creada siempre
        // quedara en la fila 0 del Array.
        System.out.println("Código de reserva: " + codigo);
        System.out.println("Tipo de habitación: " + datosReserva.get(0));
        // Comprobar si el tamaño del Array es mayor de 1, para saber que existen
        // servicios adicionales contratados.
        if (datosReserva.size() > 1) {
            System.out.println("Servicios contratados:");
            for (int i = 1; i < datosReserva.size(); i++) {
                System.out.println("- " + datosReserva.get(i));
            }
        } else {
            System.out.println("No se contrataron servicios adicionales.");
        }
    }

    /**
     * Muestra todas las reservas existentes por tipo de habitacion
     * especifica.
     */
    public static void obtindreReservaPerTipus() {
        System.out.println("\n===== CONSULTAR RESERVES PER TIPUS =====");
        // TODO: Llistar reserves per tipus

        // Usuario dic el tipo de habitación.
        String tipoElegido = seleccionarTipusHabitacio();

        if (tipoElegido == null) {
            System.out.println("Tipo de habitación no válido.");
            return;
        }

        // for-each recorre Array y saca los codigos que coincidan con el tipo de
        // habitación seleccionado.
        boolean encontrada = false;

        for (int codigo : reserves.keySet()) {
            ArrayList<String> datos = reserves.get(codigo);
            String tipoReserva = datos.get(0);

            // Condicional si el tipo elegido es igual a las reservas, se muestran los datos
            // de las reservas de ese tipo de habitación.Mientras sea cierto continua el bucle.

            if (tipoReserva.equals(tipoElegido)) {
                mostrarDadesReserva(codigo);
                encontrada = true;
            }
        }

        if (!encontrada) {
            System.out.println("No hay reservas para el tipo " + tipoElegido);
        }
    }

    

    /**
     * Consulta y muestra en detalle la informacion de una reserva.
     */
    public static void mostrarDadesReserva(int codi) {
        // TODO: Imprimir tota la informació d'una reserva

        if (!reserves.containsKey(codi)) {
            System.out.println("Codigo no encontrado: " + codi);
            return;
        }

        ArrayList<String> datos = reserves.get(codi);

        System.out.println("Código de reserva: " + codi);
        System.out.println("Tipo de habitación: " + datos.get(0));

        if (datos.size() > 1) {
            System.out.println("Servicios:");
            for (int i = 1; i < datos.size(); i++) {
                System.out.println("- " + datos.get(i));
            }
        } else {
            System.out.println("Sin servicios adicionales.");
        }
    }

    // --------- METODOS AUXILIARES (PARA MEJORAR LEGIBILIDAD) ---------

    /**
     * Lee un entero por teclado mostrando un mensaje y gestiona posibles
     * errores de entrada.
     */
    static int llegirEnter(String missatge) {
        int valor = 0;
        boolean correcte = false;
        while (!correcte) {
            System.out.print(missatge);
            valor = sc.nextInt();
            correcte = true;
        }
        return valor;
    }

    /**
     * Muestra en la pantalla informacion de un tipo de habitacion: precio y
     * habitaciones disponibles.
     */
    static void mostrarInfoTipus(String tipus) {
        int disponibles = disponibilitatHabitacions.get(tipus);
        int capacitat = capacitatInicial.get(tipus);
        float preu = preusHabitacions.get(tipus);
        System.out.println("- " + tipus + " (" + disponibles + " disponibles de " + capacitat + ") - " + preu + "€");
    }

    /**
     * Muestra la disponibilidad (libres y ocupados) de un tipo de habitacion.
     */
    static void mostrarDisponibilitatTipus(String tipus) {
        int lliures = disponibilitatHabitacions.get(tipus);
        int capacitat = capacitatInicial.get(tipus);
        int ocupades = capacitat - lliures;

        String etiqueta = tipus;
        if (etiqueta.length() < 8) {
            etiqueta = etiqueta + "\t"; // per a quadrar la taula
        }

        System.out.println(etiqueta + "\t" + lliures + "\t" + ocupades);
    }
}
