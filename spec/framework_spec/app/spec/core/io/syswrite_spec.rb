require File.dirname(File.join(__rhoGetCurrentDir(), __FILE__)) + '/../../spec_helper'
require File.dirname(File.join(__rhoGetCurrentDir(), __FILE__)) + '/fixtures/classes'
require File.dirname(File.join(__rhoGetCurrentDir(), __FILE__)) + '/shared/write'

describe "IO#syswrite on a file" do
  before :each do
    @filename = tmp("IO_syswrite_file") + $$.to_s
    File.open(@filename, "w") do |file|
      file.syswrite("012345678901234567890123456789")
    end
    @file = File.open(@filename, "r+")
    @readonly_file = File.open(@filename)
  end

  after :each do
    @file.close
    @readonly_file.close
    File.delete(@filename)
  end

  it "writes all of the string's bytes but does not buffer them" do
    written = @file.syswrite("abcde")
    written.should == 5
    File.open(@filename) do |file|
      file.sysread(10).should == "abcde56789"
      file.seek(0)
      @file.fsync
      file.sysread(10).should == "abcde56789"
    end
  end

  not_compliant_on :rubinius do
    it "warns if called immediately after a buffered IO#write" do
      @file.write("abcde")
      lambda { @file.syswrite("fghij") }.should complain(/syswrite/)
    end
  end

  it "does not warn if called after IO#write with intervening IO#sysread" do
    @file.syswrite("abcde")
    @file.sysread(5)
    lambda { @file.syswrite("fghij") }.should_not complain
  end

  it "writes to the actual file position when called after buffered IO#read" do
    @file.read(5)
    @file.syswrite("abcde")
    File.open(@filename) do |file|
      file.sysread(10).should == "01234abcde"
    end
  end
end

describe "IO#syswrite" do
  it_behaves_like :io_write, :syswrite
end
