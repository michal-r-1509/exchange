export interface InfoResponseDto {
  code: string;
  name: {
    common: string,
    official: string
  };
  region: string;
  subregion: string;
  flags: {
    svg: string
  };
}
