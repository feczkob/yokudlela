package com.yokudlela.kitchen.application

import org.modelmapper.ModelMapper
import org.springframework.stereotype.Component

@Component
class GenericConverter(private val modelMapper: ModelMapper) {
    fun <S, D> convert(
        src: S,
        dest: D
    ): D {
        modelMapper.map(src, dest)
        return dest
    }

    fun <S, D> convert(
        src: S,
        dest: Class<D>
    ): D {
        return modelMapper.map(src, dest)
    }


//    fun <S, D> convert(source: S?, destination: Class<D>?, mapper: ModelMapper): D? {
//        return if (source == null) {
//            null
//        } else mapper.map(source, destination)
//    }
//
//    fun <S, D> convert(source: S?, destination: D?, mapper: ModelMapper): D? {
//        if (source == null || destination == null) {
//            return null
//        }
//        mapper.map(source, destination)
//        return destination
//    }

}