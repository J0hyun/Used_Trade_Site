package com.mbc.dto;

import com.mbc.entity.MemberImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class MemberImgDto {

    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    private static ModelMapper modelMapper = new ModelMapper();

    public static MemberImgDto of(MemberImg memberImg) {
        return modelMapper.map(memberImg, MemberImgDto.class);
    }

}
