package com.back.route4d.controller;

import com.back.route4d.helper.Helper;
import com.back.route4d.model.CallesBloqueadas;
import com.back.route4d.model.FileInfo;
import com.back.route4d.message.ResponseMessage;
import com.back.route4d.model.Pedido;
import com.back.route4d.repository.CallesBloqueadasRepository;
import com.back.route4d.repository.PedidoRepository;
import com.back.route4d.services.FilesStorageService;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.back.route4d.services.impl.FileStorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;


@Controller
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/archivos")
public class FilesController {

    @Autowired
    FilesStorageService storageService;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    CallesBloqueadasRepository callesBloqueadasRepository;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        String message = "";
        try {
            List<String> fileNames = new ArrayList<>();

            Arrays.asList(files).stream().forEach(file -> {
                storageService.save(file);
                fileNames.add(file.getOriginalFilename());
            });

            try {
                // TODO: terminar lectura con más de un archivo
                // TODO: validar nombre del archivo
                // Por ahora, el proceso está limitado a agregar 100 pedidos por día, por los 5 primeros días
                String fileName = FileStorageServiceImpl.folderName + "/" + fileNames.get(0);
                final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
                String strYearMonth = Helper.getOrdersDateFromName(fileName); // datos del nombre del archivo
                String line; // línea del archivo
                int id = 1; // contador para identificador
                List<Pedido> listaPedidos = new ArrayList<>(); // para almacenar pedidos
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:s");

                // Leyendo datos del archivo
                int dayAnt = 0;     // eliminar luego
                int pedidoCont = 0; // eliminar luego
                while ((line = br.readLine()) != null) {
                    final String[] tokens = line.trim().split(",");
                    final String[] date = tokens[0].trim().split(":");
                    final int day = Integer.parseInt(date[0]);

                    if (dayAnt != day) { // eliminar luego
                        pedidoCont = 0;
                    }

                    if (day >= 1 && day <= 5 && pedidoCont < 100) { // eliminar luego últ. condición
                        dayAnt = day; // eliminar luego

                        final int hour = Integer.parseInt(date[1]);
                        final int min = Integer.parseInt(date[2]);
                        final int x = Integer.parseInt(tokens[1]);
                        final int y = Integer.parseInt(tokens[2]);
                        final int demand = Integer.parseInt(tokens[3]);
                        int remaining = Integer.parseInt(tokens[4]);
                        String strDate = strYearMonth + "-" + day + " " + hour + ":" + min + ":0";
                        LocalDateTime orderDate = LocalDateTime.parse(strDate, formatter);
                        LocalDateTime limitDate = orderDate.plus(Duration.of(remaining, ChronoUnit.HOURS));
                        remaining = Helper.convertLocalDateTimeToMinutes(limitDate);
                        Pedido pedido = new Pedido(id++, x, y, demand, remaining, orderDate, limitDate, 0);
                        listaPedidos.add(pedido);
                        pedidoCont++; // eliminar luego
                    }
                    else if (day > 5) {
                        break;
                    }
                }

                br.close();

                pedidoRepository.saveAll(listaPedidos);
            } catch (IOException e) {
                e.printStackTrace();
            }

            message = "Archivo cargado correctamente: " + fileNames;
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Error al cargar los archivos!";
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @PostMapping("/uploadBloqueos")
    public ResponseEntity<ResponseMessage> uploadFilesBloqueos(@RequestParam("files") MultipartFile[] files) {
        String message = "";
        try {
            List<String> fileNames = new ArrayList<>();

            Arrays.asList(files).stream().forEach(file -> {
                storageService.save(file);
                fileNames.add(file.getOriginalFilename());
            });

            try {
                String fileName = FileStorageServiceImpl.folderName + "/" + fileNames.get(0);
                final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
                String strYearMonth = Helper.getLockedNodesDateFromName(fileName); // datos del nombre del archivo
                String line; // línea del archivo
                int id = 1; // contador para identificador
                List<CallesBloqueadas> listaCallesBloqueadas = new ArrayList<>(); // para almacenar calles bloqueadas
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d H:m:s");

                while ((line = br.readLine()) != null) {
                    final String[] tokens = line.trim().split(",");
                    final String[] plazo = tokens[0].trim().split("-");
                    final String[] inicio = plazo[0].trim().split(":");
                    final String[] fin = plazo[1].trim().split(":");
                    final int diaIni = Integer.parseInt(inicio[0]);
                    final int diaFin = Integer.parseInt(fin[0]);
                    final int horaIni = Integer.parseInt(inicio[1]);
                    final int horaFin = Integer.parseInt(fin[1]);
                    final int minIni = Integer.parseInt(inicio[2]);
                    final int minFin = Integer.parseInt(fin[2]);
                    String strDateIni = strYearMonth + "-" + diaIni + " " + horaIni + ":" + minIni + ":0";
                    String strDateFin = strYearMonth + "-" + diaFin + " " + horaFin + ":" + minFin + ":0";
                    LocalDateTime dateIni = LocalDateTime.parse(strDateIni, formatter);
                    LocalDateTime dateFin = LocalDateTime.parse(strDateFin, formatter);

                    final int len = tokens.length - 1;
                    final String[] strCoords = Arrays.copyOfRange(tokens, 1, len + 1);
                    final int[] coords = new int[len];

                    for (int i = 0; i < len; i++) {
                        coords[i] = Integer.parseInt(strCoords[i]); // pasando a enteros
                    }

                    CallesBloqueadas calleBloqueada = new CallesBloqueadas(
                            id++,
                            Helper.convertLocalDateTimeToMinutes(dateIni),
                            Helper.convertLocalDateTimeToMinutes(dateFin)
                    );

                    // Agregando el identificador del nodo a la calle bloqueada

                    for (int i = 0; i < len - 2; i += 2) {
                        int x = coords[i];
                        int y = coords[i + 1];

                        int x2 = coords[i + 2];
                        int y2 = coords[i + 3];

                        if (y2 - y == 0) {
                            for (int j = x; j <= x2; j++) {
                                calleBloqueada.addNodeToString(j + 71 * y + 1);

                            }
                        } else {
                            if (x2 - x == 0) {
                                for (int k = y; k <= y2; k++) {
                                    calleBloqueada.addNodeToString(x + 71 * k + 1);
                                }
                            }
                        }
                    }
                    listaCallesBloqueadas.add(calleBloqueada);
                }
                br.close();

                callesBloqueadasRepository.saveAll(listaCallesBloqueadas);
            } catch (IOException e) {
                e.printStackTrace();
            }
            message = "Archivo cargado correctamente: " + fileNames;
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Error al cargar los archivos!";
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getListFiles() {
        List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();

            return new FileInfo(filename, url);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
