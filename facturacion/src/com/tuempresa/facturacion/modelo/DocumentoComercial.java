package com.tuempresa.facturacion.modelo;

import java.math.*;
import java.time.*;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;

import com.tuempresa.facturacion.calculadores.*;

import lombok.*;

@Entity @Getter @Setter
@View(members=
       "anyo, numero, fecha;"+
          "datos{"+
           	"cliente;"+
           	"detalles;"+
           	"observaciones"+
          "}"
       )
abstract public class DocumentoComercial extends Identificable{
    
    
    @DefaultValueCalculator(CurrentYearCalculator.class)
    @Column(length=4)
    int anyo;
    
    @Column(length=6)
    @DefaultValueCalculator(value = CalculadorSiguienteNumeroParaAnyo.class,
    properties = @PropertyValue(name="anyo"))
    int numero;
    
    @Required
    @DefaultValueCalculator(CurrentLocalDateCalculator.class)
    LocalDate fecha;
    
    @Stereotype("MEMO")
    String observaciones;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ReferenceView("simple")
    Cliente cliente;
    
    @ElementCollection
    @ListProperties("producto.numero, producto.descripcion, cantidad, precioPorUnidad, "
    			+"importe+["
    			+   "documentoComercial.porcentajeIVA,"
    			+   "documentoComercial.iva,"
    			+   "documentoComercial.importeTotal"
    			+ "]"
     )
    private Collection<Detalle> detalles;
    
    @DefaultValueCalculator(CalculadorPorcentajeIVA.class)
    @Digits(integer=2, fraction=0)
    BigDecimal porcentajeIVA;
    
    @ReadOnly
    @Stereotype("DINERO")
    @Calculation("sum(detalles.importe) * porcentajeIVA / 100")
    BigDecimal iva;
    
    @ReadOnly
    @Stereotype("DINERO")
    @Calculation("sum(detalles.importe) + iva")
    BigDecimal importeTotal;
}
