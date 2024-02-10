package com.bartoszthielmann.gitapi.mapper;

import com.bartoszthielmann.gitapi.dto.RepositoryDto;
import com.bartoszthielmann.gitapi.entity.Repository;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = BranchMapper.class, componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RepositoryMapper {

    @Mapping(source = "owner.ownerLogin", target = "ownerLogin")
    RepositoryDto repositoryToRepositoryDto(Repository repository);
}
