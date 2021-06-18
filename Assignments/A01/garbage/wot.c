int main()
{
  int p[2];
  char inputBuffer[1024];

  if (pipe(p) < 0)
  {
    printf("Error when creating the pipe");
    return 1;
  }

  if (fork() == 0)
  {
    close(p[0]);
    write(p[1], "Hello, C", sizeof(inputBuffer));
    close(p[1]);
  }else
  {
    close(p[1]);
    read(p[0], inputBuffer, sizeof(inputBuffer));
    close(p[0]);
    printf("Hello, P : %s \n", inputBuffer);
  }
  return 0;
}
