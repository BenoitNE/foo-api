package mapper;

import dto.FooRequestDTO;
import dto.FooResponseDTO;
import entity.Foo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") // Pour l'injection de dépendance par Spring
public interface FooMapper {

    FooMapper INSTANCE = Mappers.getMapper(FooMapper.class); // Si pas d'injection Spring

    Foo toEntity(FooRequestDTO fooRequestDTO);

    FooResponseDTO toResponseDTO(Foo foo);

    // List<FooResponseDTO> toResponseDTOList(List<Foo> foos); // Pour les listes
}
