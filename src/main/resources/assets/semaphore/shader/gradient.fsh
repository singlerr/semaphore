#version 110

varying vec2 f_Position;
varying vec2 texCoord0;

vec4 texelFetch(float a, vec2 input, int b){
    return vec4(input, 1.0, 1.0);
}

void main() {
      vec2 size = vec2(5.0);
      vec2 blur_size = vec2(1.0);
      float uv_x = texCoord0.x * size.x;
      float uv_y = texCoord0.y * size.y;
      float t0 = 0.0;
      vec4 sum = vec4(0.0);
      for (int n = 0; n < 9; ++n) {
          uv_y = (texCoord0.y * size.y) + (blur_size.y * float(float(n) - 4.5));
          vec4 h_sum = vec4(0.0);
          h_sum += texelFetch(t0, vec2(uv_x - (4.0 * blur_size.x), uv_y), 0);
          h_sum += texelFetch(t0, vec2(uv_x - (3.0 * blur_size.x), uv_y), 0);
          h_sum += texelFetch(t0, vec2(uv_x - (2.0 * blur_size.x), uv_y), 0);
          h_sum += texelFetch(t0, vec2(uv_x - blur_size.x, uv_y), 0);
          h_sum += texelFetch(t0, vec2(uv_x, uv_y), 0);
          h_sum += texelFetch(t0, vec2(uv_x + blur_size.x, uv_y), 0);
          h_sum += texelFetch(t0, vec2(uv_x + (2.0 * blur_size.x), uv_y), 0);
          h_sum += texelFetch(t0, vec2(uv_x + (3.0 * blur_size.x), uv_y), 0);
          h_sum += texelFetch(t0, vec2(uv_x + (4.0 * blur_size.x), uv_y), 0);
          sum += h_sum / 9.0;
      }
    gl_FragColor =  sum / 9.0;
}
