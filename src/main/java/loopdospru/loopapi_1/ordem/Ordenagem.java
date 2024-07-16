package loopdospru.loopapi_1.ordem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Ordenagem<T> {
    private OrdenagemType tipo;
    private Map<String, List<T>> modulos = new HashMap<>();
    private List<T> valores = new ArrayList<>();
    private boolean invertido = false;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private Ordenagem(OrdenagemType tipo) {
        this.tipo = tipo;
    }

    public static <T> Ordenagem<T> of(OrdenagemType tipo) {
        return new Ordenagem<>(tipo);
    }

    public void criarModulos(String... modulos) {
        for (String modulo : modulos) {
            this.modulos.put(modulo, new ArrayList<>());
        }
    }

    public void set(List<T> valores) {
        this.valores = valores;
    }

    public void setModulo(String modulo, ModuloAction<T> action) {
        List<T> listaModulo = modulos.get(modulo);
        if (listaModulo != null) {
            for (T valor : valores) {
                action.whatToOrdenar(valor);
                listaModulo.add(valor);
            }
            ordenarModulo(listaModulo);
        }
    }

    private void ordenarModulo(List<T> lista) {
        if (tipo == OrdenagemType.NUMBER) {
            lista.sort((o1, o2) -> {
                Double d1 = convertToDouble(o1);
                Double d2 = convertToDouble(o2);
                return d1.compareTo(d2);
            });
        } else if (tipo == OrdenagemType.DATE) {
            lista.sort((o1, o2) -> {
                Date d1 = convertToDate(o1.toString());
                Date d2 = convertToDate(o2.toString());
                return d1.compareTo(d2);
            });
        }

        if (invertido) {
            Collections.reverse(lista);
        }
    }

    private Double convertToDouble(T value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        throw new IllegalArgumentException("O valor não é um número");
    }

    private Date convertToDate(String value) {
        try {
            return dateFormat.parse(value);
        } catch (ParseException e) {
            try {
                dateFormat.applyPattern("dd/MM/yyyy");
                return dateFormat.parse(value);
            } catch (ParseException ex) {
                throw new IllegalArgumentException("Formato de data inválido");
            }
        }
    }

    public List<T> getOrdenagem(String modulo) {
        return modulos.getOrDefault(modulo, new ArrayList<>());
    }

    public void invert() {
        this.invertido = !this.invertido;
    }

    public interface ModuloAction<T> {
        void whatToOrdenar(T value);
    }
}
