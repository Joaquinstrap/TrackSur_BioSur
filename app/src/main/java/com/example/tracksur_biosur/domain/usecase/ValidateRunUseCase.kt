package com.example.tracksur_biosur.domain.usecase

class ValidateRunUseCase {
    //Clase cuya funcion es la validacion del run segun la normativa Chilena

    //se hace una seeled class con el objetivo de tener dos resultados
    //que la validacion sea correcta o haya habido algun error
    sealed class Result {
        object Success : Result()
        data class Error(val message: String): Result()
    }

    fun execute(run:String): Result {
        val cleanRun = run.replace(" ", "")
            .replace("-","")
            .replace(" ","")
            .trim()
            .uppercase()

        if(cleanRun.isEmpty()){
            return Result.Error("El Run del trabajador es obligatorio")
        }

        if(cleanRun.length < 8){
            return Result.Error("El run del trabajador es demasiado corto")
        }

        //Esta variable toma todo el cuerpo del rut y no tiene en cuenta el digito verificador
        val body = cleanRun.dropLast(1)
        //A diferencia del de arriba este tomas solo el digito verificador
        val dv = cleanRun.takeLast(1)

        if(!body.all{it.isDigit()}){
            return Result.Error("El cuerpo del Run no debe contener letras")
        }

        val expectDv = calculateDv(body)
        return if (dv == expectDv) {
            Result.Success
        }else{
            Result.Error("El digito verificador del Run no es valido")
        }

    }

    private fun calculateDv(body: String): String {
        var sum = 0
        var multiplier = 2

        for (i in body.reversed()){
            sum += Character.getNumericValue(i) * multiplier
            multiplier = if(multiplier == 7) 2 else multiplier + 1
        }

        val remainder = 11 - (sum % 11)
        return when(remainder){
            11 -> "0"
            10 -> "K"
            else -> remainder.toString()
        }


    }


}