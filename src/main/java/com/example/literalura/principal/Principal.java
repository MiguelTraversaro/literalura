package com.example.literalura.principal;

import com.example.literalura.model.*;
import com.example.literalura.repository.AutorRepository;
import com.example.literalura.repository.LibroRepository;
import com.example.literalura.service.ConsumoAPI;
import com.example.literalura.service.ConvierteDatos;

import java.util.*;

public class Principal {

    private Scanner lectura = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository repositorioLibro;
    private AutorRepository repositorioAutor;

    public Principal(LibroRepository repositoryLibro, AutorRepository repositoryAutor){
        this.repositorioLibro = repositoryLibro;
        this.repositorioAutor = repositoryAutor;
    }

    public void mostrarMenu(){
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1- Buscar libro por titulo
                    2- Listar libros registrados
                    3- listar autores registrados
                    4- Listar autores vivos en un determinado año
                    5- Listar Cantidad de libros por idioma
                    0- Salir
                    """;

            try {
                System.out.println(menu);
                if (lectura.hasNextInt()) {
                    opcion = lectura.nextInt();
                    lectura.nextLine();
                } else {
                    System.out.println("Ingrese un numero valido (0-5)");
                    lectura.nextLine();
                    continue;
                }

                switch (opcion){
                    case 1: buscarLibro(); break;
                    case 2: listarLibrosRegistrados(); break;
                    case 3: listarAutoresRegistrados(); break;
                    case 4: listarAutoresVivos(); break;
                    case 5: listarCantidadLibrosPorIdioma(); break;
                    case 0: System.out.println("Cerrando aplicacion..."); break;
                    default: System.out.println("Ingrese un numero valido (0-5)...");
                }
            } catch (Exception e) {
                System.out.println("Ingrese un valor valido..." + e.getMessage());
                lectura.nextLine();
            }

        }
    }

    private void listarCantidadLibrosPorIdioma() {
        List<Libro> todosLosLibros = repositorioLibro.findAll();

        if (todosLosLibros.isEmpty()){
            System.out.println("No hay libros...");
            return;
        }

        long totalLibros = todosLosLibros.size();

        List<String> idiomasAConsultar = List.of("es","en","fr","pt");

        System.out.println("------ LIBROS POR IDIOMA ------");
        System.out.println("Total de libros registrados: " + totalLibros);
        System.out.println("-------------------------------");

        idiomasAConsultar.forEach(idioma -> {
            long cantidad = todosLosLibros.stream()
                    .filter(l -> l.getIdioma().equalsIgnoreCase(idioma))
                    .count();

            double porcentaje = (double) cantidad / totalLibros * 100;

            if (cantidad > 0) {
                System.out.printf("Idioma [%s]: %d libro(s) (%.2f%% del catálogo)%n", idioma.toUpperCase(), cantidad, porcentaje);
            }
        });
    }



    private void listarAutoresVivos() {
        System.out.println("Ingresa el año que deseas consultar: ");
        try {
            var anio = lectura.nextInt();
            lectura.nextLine();

            List<Autor> autoresVivos = repositorioAutor.buscarAutoresVivos(anio);

            if(autoresVivos.isEmpty()){
                System.out.println("No se encontraron autores vivos en el año " + anio);
            } else {
                System.out.println("------ AUTORES VIVOS EN EL AÑO " + anio + " ------");
                autoresVivos.forEach(a -> System.out.println(
                        "Autor: " + a.getNombre() +
                        " | Nacimiento: " + a.getFechaNacimiento() +
                        " | Fallecimiento: " + (a.getFechaFallecimiento() != null ? a.getFechaFallecimiento() : "N/A")
                ));
            }
        } catch (InputMismatchException e) {
            System.out.println("Ingresar un numero de año valido...");
            lectura.nextLine();
        }
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = repositorioAutor.findAll();

        if (autores.isEmpty()){
            System.out.println("No hay autores registrados en la base de datos");
        } else {
            autores.stream()
                    .sorted(Comparator.comparing(Autor::getNombre))
                    .forEach(a -> System.out.println(
                            "------ AUTORES REGISTRADOS ------" + "\n" +
                            "Autor: " + a.getNombre() + "\n" +
                            "Fecha de nacimiento: " + (a.getFechaNacimiento() != null ? a.getFechaNacimiento() : "Desconocido") + "\n" +
                            "Fecha de fallecimiento: " + (a.getFechaFallecimiento() != null ? a.getFechaFallecimiento() : "Desconocido") + "\n" +
                            "Libros: " + a.getLibros().stream().map(Libro::getTitulo).toList() + "\n" +
                            "-------------------------------"
                    ));
        }
    }

    private void listarLibrosRegistrados() {
        List<Libro> libros = repositorioLibro.findAll();

        if (libros.isEmpty()){
            System.out.println("No hay libros registrados en la base de datos");
        } else {
            System.out.println("------ LIBROS REGISTRADOS ------");
            libros.stream()
                    .sorted(Comparator.comparing(Libro::getTitulo))
                    .forEach(System.out::println);
        }
    }

    private DatosLibro getDatosLibro() {
        System.out.println("Escribe el nombre del libro que quieres buscar: ");
        var nombreLibro = lectura.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        return datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toLowerCase().contains(nombreLibro.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    private void buscarLibro() {
        DatosLibro datos = getDatosLibro();


        if (datos != null){
            String nombreAutor = datos.autor().stream()
                    .map(a -> a.nombre())
                    .findFirst()
                    .orElse("Desconocido");
            Optional<Libro> libroExistente = repositorioLibro.findByTituloContainsIgnoreCase(datos.titulo());
            if (libroExistente.isPresent()){
                System.out.println("El libro ya se encuentra en la base de datos");
                System.out.println("------ LIBRO ------" + "\n" +
                        "Título: " + datos.titulo() + "\n" +
                        "Autor: " + (nombreAutor) + "\n" +
                        "Idioma: " + datos.idiomas().getFirst() + "\n" +
                        "Número de descargas: " + datos.numeroDeDescargas() + "\n" +
                        "-------------------");
                return;
            }

            DatosAutor datosAutor = datos.autor().getFirst();
            Autor autor = repositorioAutor.findByNombreContainsIgnoreCase(datosAutor.nombre())
                            .orElseGet(()->{
                                Autor nuevoAutor = new Autor(datosAutor);
                                return repositorioAutor.save(nuevoAutor);
                            });

            Libro libro = new Libro(datos);
            libro.setAutor(autor);
            repositorioLibro.save(libro);

            System.out.println("Libro guardado con exito: " + libro.getTitulo());
            System.out.println("------ LIBRO ------" + "\n" +
                    "Título: " + datos.titulo() + "\n" +
                    "Autor: " + (nombreAutor) + "\n" +
                    "Idioma: " + datos.idiomas().getFirst() + "\n" +
                    "Número de descargas: " + datos.numeroDeDescargas() + "\n" +
                    "-------------------");
        } else {
            System.out.println("Libro no encontrado...");
        }
    }
}
