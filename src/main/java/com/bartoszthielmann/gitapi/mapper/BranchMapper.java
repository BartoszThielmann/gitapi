package com.bartoszthielmann.gitapi.mapper;

import com.bartoszthielmann.gitapi.dto.BranchDto;
import com.bartoszthielmann.gitapi.entity.Branch;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BranchMapper {
    @Mapping(source = "commit.sha", target = "sha")
    BranchDto branchToBranchDto(Branch branch);
}
